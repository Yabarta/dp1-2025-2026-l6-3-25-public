import React from 'react';
import ScoreBar from './ScoreBar';

export default function PlayerColumn({ player, fallbackLabel, score, style }) {
  const displayName = player?.nickname ?? player?.username ?? fallbackLabel;
  const safeScore = score ?? 0;

  return (
    <div className="playerColumn">
      <div className="playerName" style={{ color: style.nameColor }}>
        {displayName}
      </div>
      <ScoreBar score={safeScore} color={style.color} />
    </div>
  );
}
