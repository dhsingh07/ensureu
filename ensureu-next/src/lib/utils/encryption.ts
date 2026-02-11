// AES Encryption/Decryption - migrated from Angular encr-decr.service.ts

import CryptoJS from 'crypto-js';

// Encryption configuration (must match Angular/backend)
const ENCRYPTION_CONFIG = {
  IV: process.env.NEXT_PUBLIC_ENCRYPTION_IV || '7d88998f7bb1a35f17d39e23b43775b0',
  SALT: process.env.NEXT_PUBLIC_ENCRYPTION_SALT || 'a7961e94564f046d493621714c296ca6',
  PASSPHRASE: process.env.NEXT_PUBLIC_ENCRYPTION_PASSPHRASE || 'ensureu@123',
  ITERATIONS: 1000,
  KEY_SIZE: 128 / 32, // 4
};

/**
 * Get passphrase - simplified without date enrichment for debugging
 * TODO: Re-enable date enrichment after encryption issue is fixed
 */
function getPassphrase(_withDate: boolean): string {
  // SIMPLIFIED: Always use plain passphrase without date
  // This makes debugging easier and removes timezone issues
  return ENCRYPTION_CONFIG.PASSPHRASE;
}

/**
 * Test decryption with a known encrypted value
 * Call this from browser console: testDecryption()
 */
export function testDecryption(encryptedData: string): void {
  console.log('=== Decryption Test ===');
  console.log('Config:', {
    iv: ENCRYPTION_CONFIG.IV,
    salt: ENCRYPTION_CONFIG.SALT,
    passphrase: ENCRYPTION_CONFIG.PASSPHRASE,
    keySize: ENCRYPTION_CONFIG.KEY_SIZE,
    iterations: ENCRYPTION_CONFIG.ITERATIONS,
  });

  const currentDate = new Date().toISOString().split('T')[0];
  console.log('Current date (ISO):', currentDate);

  // Try without date
  console.log('\n--- Trying without date ---');
  console.log('Passphrase:', ENCRYPTION_CONFIG.PASSPHRASE);
  try {
    const result = decrypt(encryptedData, false);
    console.log('Success! Result:', result);
  } catch (e) {
    console.log('Failed:', e);
  }

  // Try with date
  console.log('\n--- Trying with date ---');
  console.log('Passphrase:', ENCRYPTION_CONFIG.PASSPHRASE + currentDate);
  try {
    const result = decrypt(encryptedData, true);
    console.log('Success! Result:', result);
  } catch (e) {
    console.log('Failed:', e);
  }
}

/**
 * Encrypt data using AES
 * @param data - Data to encrypt (string or object)
 * @param withDate - If true, enrich passphrase with current date (required for paper data)
 * @returns Encrypted string
 */
export function encrypt(data: string | object, withDate = false): string {
  const text = typeof data === 'object' ? JSON.stringify(data) : String(data);
  const passphrase = getPassphrase(withDate);

  if (process.env.NODE_ENV === 'development') {
    console.log('[encrypt] Passphrase:', passphrase);
    console.log('[encrypt] Salt:', ENCRYPTION_CONFIG.SALT);
    console.log('[encrypt] IV:', ENCRYPTION_CONFIG.IV);
    console.log('[encrypt] Data length:', text.length);
  }

  // Backend uses PBKDF2WithHmacSHA1 - must specify SHA1 hasher
  const key = CryptoJS.PBKDF2(
    passphrase,
    CryptoJS.enc.Hex.parse(ENCRYPTION_CONFIG.SALT),
    {
      keySize: ENCRYPTION_CONFIG.KEY_SIZE,
      iterations: ENCRYPTION_CONFIG.ITERATIONS,
      hasher: CryptoJS.algo.SHA1,
    }
  );

  const encrypted = CryptoJS.AES.encrypt(text, key, {
    iv: CryptoJS.enc.Hex.parse(ENCRYPTION_CONFIG.IV),
  });

  const result = encrypted.ciphertext.toString(CryptoJS.enc.Base64);

  if (process.env.NODE_ENV === 'development') {
    console.log('[encrypt] Encrypted length:', result.length);
    console.log('[encrypt] Encrypted (first 100 chars):', result.substring(0, 100) + '...');

    // Verify by attempting to decrypt
    try {
      const testDecrypt = tryDecrypt(result, passphrase);
      console.log('[encrypt] Verify decrypt success:', !!testDecrypt);
    } catch (e) {
      console.error('[encrypt] Verify decrypt failed:', e);
    }
  }

  return result;
}

/**
 * Attempt decryption with a specific passphrase
 * IMPORTANT: Backend uses PBKDF2WithHmacSHA1, so we must use SHA1 hasher
 */
function tryDecrypt(encryptedData: string, passphrase: string): string | null {
  try {
    // Backend uses PBKDF2WithHmacSHA1 - must specify SHA1 hasher
    // CryptoJS v4.x defaults to SHA256, which won't work with our backend
    const key = CryptoJS.PBKDF2(
      passphrase,
      CryptoJS.enc.Hex.parse(ENCRYPTION_CONFIG.SALT),
      {
        keySize: ENCRYPTION_CONFIG.KEY_SIZE,
        iterations: ENCRYPTION_CONFIG.ITERATIONS,
        hasher: CryptoJS.algo.SHA1,
      }
    );

    const cipherParams = CryptoJS.lib.CipherParams.create({
      ciphertext: CryptoJS.enc.Base64.parse(encryptedData),
    });

    const decrypted = CryptoJS.AES.decrypt(cipherParams, key, {
      iv: CryptoJS.enc.Hex.parse(ENCRYPTION_CONFIG.IV),
    });

    const result = decrypted.toString(CryptoJS.enc.Utf8);
    return result || null;
  } catch {
    return null;
  }
}

/**
 * Get date string in YYYY-MM-DD format for a given date offset
 */
function getDateString(daysOffset: number): string {
  const date = new Date();
  date.setDate(date.getDate() + daysOffset);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

/**
 * Decrypt AES encrypted data
 * @param encryptedData - Encrypted string
 * @param _withDate - Ignored for now (simplified without date enrichment)
 * @param silent - If true, don't log errors (useful for non-critical data like practice)
 * @returns Decrypted string or parsed object
 */
export function decrypt<T = string>(encryptedData: string, _withDate = false, silent = false): T {
  // SIMPLIFIED: Always use plain passphrase without date
  const passphrase = ENCRYPTION_CONFIG.PASSPHRASE;

  if (process.env.NODE_ENV === 'development' && !silent) {
    console.log('[decrypt] Using passphrase:', passphrase);
  }

  const result = tryDecrypt(encryptedData, passphrase);
  if (result) {
    if (process.env.NODE_ENV === 'development' && !silent) {
      console.log('[decrypt] Success!');
    }
    try {
      return JSON.parse(result) as T;
    } catch {
      return result as unknown as T;
    }
  }

  if (!silent) {
    console.error('[decrypt] Decryption failed with passphrase:', passphrase);
  }
  throw new Error('Decryption failed - invalid key or corrupted data');
}
