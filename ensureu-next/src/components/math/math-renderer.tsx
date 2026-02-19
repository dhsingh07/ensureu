'use client';

import { useEffect, useRef } from 'react';
import katex from 'katex';
import 'katex/dist/katex.min.css';

interface MathRendererProps {
  content: string;
  className?: string;
  displayMode?: boolean;
}

/**
 * Renders text with LaTeX math expressions.
 * Math expressions should be wrapped in:
 * - $...$ for inline math
 * - $$...$$ for display/block math
 */
export function MathRenderer({ content, className = '', displayMode = false }: MathRendererProps) {
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!containerRef.current || !content) return;

    // Process the content to render math expressions
    const rendered = renderMathInText(content, displayMode);
    containerRef.current.innerHTML = rendered;
  }, [content, displayMode]);

  return <div ref={containerRef} className={className} />;
}

/**
 * Renders a single LaTeX expression
 */
export function MathExpression({
  latex,
  displayMode = false,
  className = ''
}: {
  latex: string;
  displayMode?: boolean;
  className?: string;
}) {
  const containerRef = useRef<HTMLSpanElement>(null);

  useEffect(() => {
    if (!containerRef.current || !latex) return;

    try {
      katex.render(latex, containerRef.current, {
        throwOnError: false,
        displayMode,
        strict: false,
      });
    } catch (error) {
      console.error('KaTeX render error:', error);
      containerRef.current.textContent = latex;
    }
  }, [latex, displayMode]);

  return <span ref={containerRef} className={className} />;
}

/**
 * Process text and render LaTeX expressions inline
 */
function renderMathInText(text: string, defaultDisplayMode: boolean = false): string {
  if (!text) return '';

  // Replace $$...$$ with display math
  let result = text.replace(/\$\$(.*?)\$\$/g, (_, latex) => {
    try {
      return katex.renderToString(latex, {
        throwOnError: false,
        displayMode: true,
        strict: false,
      });
    } catch {
      return `$$${latex}$$`;
    }
  });

  // Replace $...$ with inline math
  result = result.replace(/\$([^$]+)\$/g, (_, latex) => {
    try {
      return katex.renderToString(latex, {
        throwOnError: false,
        displayMode: false,
        strict: false,
      });
    } catch {
      return `$${latex}$`;
    }
  });

  return result;
}

/**
 * Common math symbols reference
 */
export const MATH_SYMBOLS = {
  // Basic operations
  plus: '+',
  minus: '-',
  times: '\\times',
  divide: '\\div',
  equals: '=',
  notEquals: '\\neq',

  // Comparisons
  lessThan: '<',
  greaterThan: '>',
  lessOrEqual: '\\leq',
  greaterOrEqual: '\\geq',

  // Powers and roots
  squared: '^2',
  cubed: '^3',
  power: '^{n}',
  sqrt: '\\sqrt{}',
  cbrt: '\\sqrt[3]{}',
  nthRoot: '\\sqrt[n]{}',

  // Fractions
  fraction: '\\frac{a}{b}',

  // Greek letters
  pi: '\\pi',
  theta: '\\theta',
  alpha: '\\alpha',
  beta: '\\beta',
  gamma: '\\gamma',
  delta: '\\delta',
  sigma: '\\sigma',
  lambda: '\\lambda',

  // Calculus
  integral: '\\int',
  sum: '\\sum',
  limit: '\\lim',
  infinity: '\\infty',

  // Geometry
  degree: '^\\circ',
  angle: '\\angle',
  parallel: '\\parallel',
  perpendicular: '\\perp',
  triangle: '\\triangle',

  // Sets
  union: '\\cup',
  intersection: '\\cap',
  subset: '\\subset',
  superset: '\\supset',
  element: '\\in',
  notElement: '\\notin',

  // Arrows
  rightArrow: '\\rightarrow',
  leftArrow: '\\leftarrow',
  implies: '\\Rightarrow',

  // Misc
  therefore: '\\therefore',
  because: '\\because',
  approximately: '\\approx',
  proportional: '\\propto',
  percent: '\\%',
};
