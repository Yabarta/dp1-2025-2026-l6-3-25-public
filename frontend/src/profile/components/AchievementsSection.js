import React from 'react';

export default function AchievementsSection({ Achievements = [], UserAchievements = [], achievementProgress }) {
  return (
    <div className="bg">
      <h1 className="title">Logros</h1>
      <h4>Completado {UserAchievements.length}/{Achievements.length}</h4>
      <div className="mainStatContainer">
        {Achievements.map(achievement => {
          const isCompleted = UserAchievements.some(a => a.id === achievement.id);
          return (
            <div key={achievement.id} className={`achievement ${isCompleted ? 'completed' : ''}`}>
              <div className="achievementHeader">
                <img src={achievement.icon} alt={achievement.name} className="achievementIcon" />
                <h3 className="achievementName">{achievement.name}</h3>
                <p className="achievementProgress">{achievementProgress(achievement)}</p>
              </div>
              <div className="achievementInfo">
                <p className="achievementDescriptionContainer achievementDescription">{achievement.description}</p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
