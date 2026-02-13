import { ImageResponse } from 'next/og';

export const runtime = 'edge';

export const alt = 'EnsureU - Assessment Platform for SSC & Bank Exams';
export const size = {
  width: 1200,
  height: 630,
};
export const contentType = 'image/png';

export default async function Image() {
  return new ImageResponse(
    (
      <div
        style={{
          height: '100%',
          width: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%)',
          position: 'relative',
        }}
      >
        {/* Background pattern */}
        <div
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundImage:
              'radial-gradient(circle at 25% 25%, rgba(20, 184, 166, 0.15) 0%, transparent 50%), radial-gradient(circle at 75% 75%, rgba(6, 182, 212, 0.15) 0%, transparent 50%)',
            display: 'flex',
          }}
        />

        {/* Content */}
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 10,
          }}
        >
          {/* Logo */}
          <div
            style={{
              width: 120,
              height: 120,
              background: 'linear-gradient(135deg, #14b8a6 0%, #06b6d4 100%)',
              borderRadius: 24,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: 40,
              boxShadow: '0 25px 50px -12px rgba(20, 184, 166, 0.4)',
            }}
          >
            <svg
              width="70"
              height="70"
              viewBox="0 0 24 24"
              fill="none"
              stroke="white"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M12 3l1.5 3.5L17 8l-3.5 1.5L12 13l-1.5-3.5L7 8l3.5-1.5L12 3z" />
              <path d="M5 17l1 2.5L8.5 21 6 19.5 5 17z" />
              <path d="M19 17l-1 2.5L15.5 21 18 19.5 19 17z" />
            </svg>
          </div>

          {/* Title */}
          <div
            style={{
              fontSize: 72,
              fontWeight: 800,
              background: 'linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%)',
              backgroundClip: 'text',
              color: 'transparent',
              marginBottom: 20,
              display: 'flex',
            }}
          >
            EnsureU
          </div>

          {/* Tagline */}
          <div
            style={{
              fontSize: 32,
              color: '#94a3b8',
              marginBottom: 50,
              display: 'flex',
            }}
          >
            Assessment Platform for Competitive Exams
          </div>

          {/* Exam badges */}
          <div
            style={{
              display: 'flex',
              gap: 20,
            }}
          >
            {['SSC CGL', 'SSC CPO', 'SSC CHSL', 'Bank PO'].map((exam) => (
              <div
                key={exam}
                style={{
                  background: 'rgba(20, 184, 166, 0.2)',
                  border: '2px solid rgba(20, 184, 166, 0.5)',
                  borderRadius: 12,
                  padding: '12px 24px',
                  fontSize: 24,
                  color: '#14b8a6',
                  fontWeight: 600,
                  display: 'flex',
                }}
              >
                {exam}
              </div>
            ))}
          </div>
        </div>

        {/* Footer */}
        <div
          style={{
            position: 'absolute',
            bottom: 40,
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            color: '#64748b',
            fontSize: 20,
          }}
        >
          <span>ensureu.com</span>
        </div>
      </div>
    ),
    {
      ...size,
    }
  );
}
