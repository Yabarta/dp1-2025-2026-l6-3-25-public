import React from 'react';

export default function StatsSection({ playerData = {}, playerStats = {} }) {
  return (
    <div className="mainStatContainer">
      <div className="statItem">
        <span className="statLabel">Fecha de Creación</span>
        <span className="statValue">{playerData.createdAt ? new Date(playerData.createdAt).toLocaleDateString() : new Date().toLocaleDateString()}</span>
      </div>
      <div className="statItem">
        <span className="statLabel">Tiempo de Juego</span>
        <span className="statValue">{playerStats?.timePlayed / 60 || 0} minutos</span>
      </div>
      <div className="statItem">
        <span className="statLabel">Partidas Online</span>
        <span className="statValue">{playerStats?.gamesPlayed || 0}</span>
      </div>
      <div className="statItem">
        <span className="statLabel">Victorias</span>
        <span className="statValue">{playerStats?.gamesWon || 0}</span>
      </div>
      <div className="statItem">
        <span className="statLabel">Derrotas</span>
        <span className="statValue">{playerStats?.gamesPlayed - playerStats?.gamesWon || 0}</span>
      </div>
      <div className="statItem">
        <span className="statLabel">Sarcinas</span>
        <span className="statValue">{playerStats?.sarcinasCreated || 0}</span>
      </div>
    </div>
  );
}
