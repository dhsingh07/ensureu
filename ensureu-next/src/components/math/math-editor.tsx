'use client';

import { useState, useRef, useCallback } from 'react';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { MathExpression } from './math-renderer';
import {
  Plus,
  Minus,
  X,
  Divide,
  Equal,
  Radical,
  Superscript,
  Pi,
  Sigma,
  ChevronDown,
} from 'lucide-react';

interface MathEditorProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  rows?: number;
  className?: string;
}

interface MathSymbol {
  label: string;
  latex: string;
  preview?: string;
  insert?: string; // What to insert (with cursor position marker |)
}

const SYMBOL_GROUPS = {
  basic: {
    label: 'Basic',
    symbols: [
      { label: '+', latex: '+', insert: '+' },
      { label: '-', latex: '-', insert: '-' },
      { label: '\u00D7', latex: '\\times', insert: '\\times ' },
      { label: '\u00F7', latex: '\\div', insert: '\\div ' },
      { label: '=', latex: '=', insert: '=' },
      { label: '\u2260', latex: '\\neq', insert: '\\neq ' },
      { label: '<', latex: '<', insert: '<' },
      { label: '>', latex: '>', insert: '>' },
      { label: '\u2264', latex: '\\leq', insert: '\\leq ' },
      { label: '\u2265', latex: '\\geq', insert: '\\geq ' },
      { label: '\u00B1', latex: '\\pm', insert: '\\pm ' },
      { label: '%', latex: '\\%', insert: '\\% ' },
    ],
  },
  powers: {
    label: 'Powers & Roots',
    symbols: [
      { label: 'x\u00B2', latex: 'x^2', insert: '^2' },
      { label: 'x\u00B3', latex: 'x^3', insert: '^3' },
      { label: 'x\u207F', latex: 'x^n', insert: '^{|}' },
      { label: '\u221A', latex: '\\sqrt{x}', insert: '\\sqrt{|}' },
      { label: '\u221B', latex: '\\sqrt[3]{x}', insert: '\\sqrt[3]{|}' },
      { label: '\u207F\u221A', latex: '\\sqrt[n]{x}', insert: '\\sqrt[|]{}' },
      { label: 'a/b', latex: '\\frac{a}{b}', insert: '\\frac{|}{}'  },
      { label: 'x_n', latex: 'x_n', insert: '_{|}' },
    ],
  },
  greek: {
    label: 'Greek',
    symbols: [
      { label: '\u03C0', latex: '\\pi', insert: '\\pi ' },
      { label: '\u03B8', latex: '\\theta', insert: '\\theta ' },
      { label: '\u03B1', latex: '\\alpha', insert: '\\alpha ' },
      { label: '\u03B2', latex: '\\beta', insert: '\\beta ' },
      { label: '\u03B3', latex: '\\gamma', insert: '\\gamma ' },
      { label: '\u03B4', latex: '\\delta', insert: '\\delta ' },
      { label: '\u03C3', latex: '\\sigma', insert: '\\sigma ' },
      { label: '\u03BB', latex: '\\lambda', insert: '\\lambda ' },
      { label: '\u03BC', latex: '\\mu', insert: '\\mu ' },
      { label: '\u03C9', latex: '\\omega', insert: '\\omega ' },
      { label: '\u0394', latex: '\\Delta', insert: '\\Delta ' },
      { label: '\u03A3', latex: '\\Sigma', insert: '\\Sigma ' },
    ],
  },
  geometry: {
    label: 'Geometry',
    symbols: [
      { label: '\u00B0', latex: '^\\circ', insert: '^\\circ ' },
      { label: '\u2220', latex: '\\angle', insert: '\\angle ' },
      { label: '\u25B3', latex: '\\triangle', insert: '\\triangle ' },
      { label: '\u2225', latex: '\\parallel', insert: '\\parallel ' },
      { label: '\u22A5', latex: '\\perp', insert: '\\perp ' },
      { label: '\u2245', latex: '\\cong', insert: '\\cong ' },
      { label: '\u223C', latex: '\\sim', insert: '\\sim ' },
      { label: '\u221E', latex: '\\infty', insert: '\\infty ' },
    ],
  },
  calculus: {
    label: 'Calculus',
    symbols: [
      { label: '\u222B', latex: '\\int', insert: '\\int ' },
      { label: '\u2211', latex: '\\sum', insert: '\\sum ' },
      { label: '\u220F', latex: '\\prod', insert: '\\prod ' },
      { label: 'lim', latex: '\\lim', insert: '\\lim_{| \\to } ' },
      { label: 'd/dx', latex: '\\frac{d}{dx}', insert: '\\frac{d}{dx} ' },
      { label: '\u2202', latex: '\\partial', insert: '\\partial ' },
      { label: 'log', latex: '\\log', insert: '\\log ' },
      { label: 'ln', latex: '\\ln', insert: '\\ln ' },
    ],
  },
  trig: {
    label: 'Trigonometry',
    symbols: [
      { label: 'sin', latex: '\\sin', insert: '\\sin ' },
      { label: 'cos', latex: '\\cos', insert: '\\cos ' },
      { label: 'tan', latex: '\\tan', insert: '\\tan ' },
      { label: 'cot', latex: '\\cot', insert: '\\cot ' },
      { label: 'sec', latex: '\\sec', insert: '\\sec ' },
      { label: 'csc', latex: '\\csc', insert: '\\csc ' },
      { label: 'sin\u207B\u00B9', latex: '\\sin^{-1}', insert: '\\sin^{-1} ' },
      { label: 'cos\u207B\u00B9', latex: '\\cos^{-1}', insert: '\\cos^{-1} ' },
    ],
  },
  arrows: {
    label: 'Arrows',
    symbols: [
      { label: '\u2192', latex: '\\rightarrow', insert: '\\rightarrow ' },
      { label: '\u2190', latex: '\\leftarrow', insert: '\\leftarrow ' },
      { label: '\u21D2', latex: '\\Rightarrow', insert: '\\Rightarrow ' },
      { label: '\u21D0', latex: '\\Leftarrow', insert: '\\Leftarrow ' },
      { label: '\u2194', latex: '\\leftrightarrow', insert: '\\leftrightarrow ' },
      { label: '\u21D4', latex: '\\Leftrightarrow', insert: '\\Leftrightarrow ' },
    ],
  },
  sets: {
    label: 'Sets',
    symbols: [
      { label: '\u222A', latex: '\\cup', insert: '\\cup ' },
      { label: '\u2229', latex: '\\cap', insert: '\\cap ' },
      { label: '\u2282', latex: '\\subset', insert: '\\subset ' },
      { label: '\u2286', latex: '\\subseteq', insert: '\\subseteq ' },
      { label: '\u2208', latex: '\\in', insert: '\\in ' },
      { label: '\u2209', latex: '\\notin', insert: '\\notin ' },
      { label: '\u2205', latex: '\\emptyset', insert: '\\emptyset ' },
      { label: '\u2200', latex: '\\forall', insert: '\\forall ' },
      { label: '\u2203', latex: '\\exists', insert: '\\exists ' },
    ],
  },
};

// Quick toolbar symbols (most used)
const QUICK_SYMBOLS: MathSymbol[] = [
  { label: '\u00D7', latex: '\\times', insert: '\\times ' },
  { label: '\u00F7', latex: '\\div', insert: '\\div ' },
  { label: 'x\u00B2', latex: 'x^2', insert: '^2' },
  { label: '\u221A', latex: '\\sqrt{x}', insert: '\\sqrt{|}' },
  { label: 'a/b', latex: '\\frac{a}{b}', insert: '\\frac{|}{}'  },
  { label: '\u03C0', latex: '\\pi', insert: '\\pi ' },
  { label: '\u00B0', latex: '^\\circ', insert: '^\\circ ' },
  { label: '\u221E', latex: '\\infty', insert: '\\infty ' },
];

export function MathEditor({
  value,
  onChange,
  placeholder = 'Enter text with math... Use $...$ for inline math',
  rows = 4,
  className = '',
}: MathEditorProps) {
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const [showPreview, setShowPreview] = useState(false);

  const insertSymbol = useCallback((insert: string) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const text = value;

    // Check if we need to wrap in $...$
    const needsWrapper = insert.includes('\\') && !isInsideMath(text, start);

    let toInsert = insert;
    let cursorOffset = insert.length;

    // Find cursor position marker |
    const cursorMarker = insert.indexOf('|');
    if (cursorMarker !== -1) {
      toInsert = insert.replace('|', '');
      cursorOffset = cursorMarker;
    }

    if (needsWrapper) {
      toInsert = `$${toInsert}$`;
      cursorOffset += 1; // Account for opening $
    }

    const newValue = text.substring(0, start) + toInsert + text.substring(end);
    onChange(newValue);

    // Set cursor position after insert
    setTimeout(() => {
      textarea.focus();
      const newPosition = start + cursorOffset;
      textarea.setSelectionRange(newPosition, newPosition);
    }, 0);
  }, [value, onChange]);

  const wrapSelection = useCallback((before: string, after: string) => {
    const textarea = textareaRef.current;
    if (!textarea) return;

    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const selectedText = value.substring(start, end);

    const newValue = value.substring(0, start) + before + selectedText + after + value.substring(end);
    onChange(newValue);

    setTimeout(() => {
      textarea.focus();
      textarea.setSelectionRange(start + before.length, end + before.length);
    }, 0);
  }, [value, onChange]);

  return (
    <div className={`space-y-2 ${className}`}>
      {/* Toolbar */}
      <div className="flex flex-wrap items-center gap-1 p-2 bg-slate-50 rounded-lg border">
        {/* Quick symbols */}
        <TooltipProvider>
          {QUICK_SYMBOLS.map((sym, idx) => (
            <Tooltip key={idx}>
              <TooltipTrigger asChild>
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  className="h-8 w-8 p-0 font-mono"
                  onClick={() => insertSymbol(sym.insert || sym.latex)}
                >
                  {sym.label}
                </Button>
              </TooltipTrigger>
              <TooltipContent>
                <MathExpression latex={sym.latex} />
              </TooltipContent>
            </Tooltip>
          ))}
        </TooltipProvider>

        <div className="w-px h-6 bg-slate-300 mx-1" />

        {/* Wrap in math */}
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                className="h-8 px-2 text-xs"
                onClick={() => wrapSelection('$', '$')}
              >
                $...$
              </Button>
            </TooltipTrigger>
            <TooltipContent>Wrap selection in inline math</TooltipContent>
          </Tooltip>
        </TooltipProvider>

        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                type="button"
                variant="ghost"
                size="sm"
                className="h-8 px-2 text-xs"
                onClick={() => wrapSelection('$$', '$$')}
              >
                $$...$$
              </Button>
            </TooltipTrigger>
            <TooltipContent>Wrap selection in block math</TooltipContent>
          </Tooltip>
        </TooltipProvider>

        <div className="w-px h-6 bg-slate-300 mx-1" />

        {/* More symbols popover */}
        <Popover>
          <PopoverTrigger asChild>
            <Button type="button" variant="ghost" size="sm" className="h-8 px-2">
              More <ChevronDown className="h-3 w-3 ml-1" />
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-80 p-2" align="start">
            <Tabs defaultValue="basic">
              <TabsList className="w-full flex-wrap h-auto gap-1 bg-transparent">
                {Object.entries(SYMBOL_GROUPS).map(([key, group]) => (
                  <TabsTrigger
                    key={key}
                    value={key}
                    className="text-xs px-2 py-1 data-[state=active]:bg-slate-200"
                  >
                    {group.label}
                  </TabsTrigger>
                ))}
              </TabsList>
              {Object.entries(SYMBOL_GROUPS).map(([key, group]) => (
                <TabsContent key={key} value={key} className="mt-2">
                  <div className="grid grid-cols-6 gap-1">
                    {group.symbols.map((sym, idx) => (
                      <TooltipProvider key={idx}>
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <Button
                              type="button"
                              variant="outline"
                              size="sm"
                              className="h-8 w-full p-0 font-mono text-sm"
                              onClick={() => {
                                insertSymbol(sym.insert || sym.latex);
                              }}
                            >
                              {sym.label}
                            </Button>
                          </TooltipTrigger>
                          <TooltipContent>
                            <MathExpression latex={sym.latex} />
                          </TooltipContent>
                        </Tooltip>
                      </TooltipProvider>
                    ))}
                  </div>
                </TabsContent>
              ))}
            </Tabs>
          </PopoverContent>
        </Popover>

        <div className="flex-1" />

        {/* Preview toggle */}
        <Button
          type="button"
          variant={showPreview ? 'secondary' : 'ghost'}
          size="sm"
          className="h-8 px-2 text-xs"
          onClick={() => setShowPreview(!showPreview)}
        >
          {showPreview ? 'Edit' : 'Preview'}
        </Button>
      </div>

      {/* Editor / Preview */}
      {showPreview ? (
        <div className="min-h-[100px] p-3 border rounded-md bg-white">
          <MathPreview content={value} />
        </div>
      ) : (
        <Textarea
          ref={textareaRef}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          rows={rows}
          className="font-mono text-sm"
        />
      )}

      {/* Help text */}
      <p className="text-xs text-slate-500">
        Tip: Use <code className="bg-slate-100 px-1 rounded">$...$</code> for inline math,
        <code className="bg-slate-100 px-1 rounded ml-1">$$...$$</code> for block math
      </p>
    </div>
  );
}

// Helper to check if cursor is inside math delimiters
function isInsideMath(text: string, position: number): boolean {
  const before = text.substring(0, position);
  const singleDollarCount = (before.match(/(?<!\$)\$(?!\$)/g) || []).length;
  const doubleDollarCount = (before.match(/\$\$/g) || []).length;

  // If odd number of single $ (and not inside $$), we're inside math
  return (singleDollarCount % 2 === 1) || (doubleDollarCount % 2 === 1);
}

// Preview component using MathRenderer
import { MathRenderer } from './math-renderer';

function MathPreview({ content }: { content: string }) {
  if (!content) {
    return <span className="text-slate-400 italic">Nothing to preview</span>;
  }
  return <MathRenderer content={content} className="prose prose-sm max-w-none" />;
}
