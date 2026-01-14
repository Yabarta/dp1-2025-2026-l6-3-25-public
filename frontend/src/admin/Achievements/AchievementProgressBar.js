import React, { useCallback } from "react";
import '../../static/css/admin/progressBar.css'

export default function AchievementProgressBar ({ achievement, playerStats }) {
  const getStatValue = useCallback((name) => {
    if (!playerStats) return 0;
    const sanitizedName = name.toLowerCase();
    const lowerCamelCaseName = sanitizedName.replace(/_([a-z])/g, (g) => g[1].toUpperCase());
    const value = playerStats?.[lowerCamelCaseName];
    if (value === null || value === undefined) return 0;
    return value;
  }, [playerStats]);

  const achievementProgress = useCallback((ach) => {
    const progress = Math.round(getStatValue(ach.statisticName));
    const target = ach.valor;
    return Math.min(progress, target);
  }, [getStatValue]);

  const target = Math.max(1, achievement.valor || 0); // avoid division by zero
  const percent = Math.min(100, Math.max(0, (achievementProgress(achievement) / target) * 100));

  return (
    <div 
      className="progress-container"
      role="progressbar"
      aria-valuenow={achievementProgress(achievement)}
      aria-valuemin="0"
      aria-valuemax={achievement.valor}
    >
      <div 
        className="progress-filler" 
        style={{ width: `${percent}%` }}
      >
        <span className="progress-label">{`${Math.round(percent)}%`}</span>
      </div>
    </div>
  );
}