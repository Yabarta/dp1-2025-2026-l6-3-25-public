import React from 'react';

const MAX_SCORE = 9;

export default function ScoreBar({ score = 0, color = '#888' }) {
  const clamped = Math.max(0, Math.min(MAX_SCORE, Number(score) || 0));
  const fillPercent = (clamped / MAX_SCORE) * 100;
  const ticks = Array.from({ length: MAX_SCORE + 1 }, (_, index) => index);

  return (
    <div className="scoreBarContainer">
      <div className="scoreBarFrame">
        <div
          className="scoreBarFill"
          style={{ height: `${fillPercent}%`, background: `linear-gradient(0deg, ${color} 0%, rgba(60, 7, 85, 0.9) 100%)` }}
        />
        {ticks.map((value) => {
          const percent = 100 - (value / MAX_SCORE) * 100;
          return <span key={`line-${value}`} className="scoreBarTick" style={{ top: `${percent}%` }} />;
        })}
        {ticks.map((value) => {
          const percent = 100 - (value / MAX_SCORE) * 100;
          return (
            <span key={`label-${value}`} className="scoreBarLabel" style={{ top: `${percent}%` }}>
              {value}
            </span>
          );
        })}
      </div>
    </div>
  );
}
