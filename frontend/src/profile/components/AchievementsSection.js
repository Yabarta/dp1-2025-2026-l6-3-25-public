import React from 'react';
import trofeo from '../../static/images/trofeo.png';
import AchievementGrid from '../../admin/Achievements/AchievementGrid';

export default function AchievementsSection({ Achievements = [], UserAchievements = [] }) {
  return (
    <div className="bg">
      <h1 className="title">Logros</h1>
      <h4>Completado {UserAchievements.length}/{Achievements.length}</h4>
      <div className="mainStatContainer">
        <AchievementGrid
          achievements={Achievements}
          isAdmin={false}
        />
      </div>
    </div>
  );
}
