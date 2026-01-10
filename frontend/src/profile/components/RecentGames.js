import React from 'react';
import { Button } from 'reactstrap';

export default function RecentGames({ userGames = [], isWinner, duracion, getPlayerProfilePic, handleNavigateToProfile, setShowHistoryPopup, navigate }) {
  return (
    <div className="bg">
      <h1 className="title">Partidas Recientes</h1>
      <div className="recentGamesContainer">
        {userGames.slice(0, 3).map((game) => (
          <div key={game.id} className={isWinner(game) ? 'gameWinBg' : 'gameLoseBg'}>
            <div className="gameHeader">
              <div className="gameResult">
                {isWinner(game) ? 'Victoria' : 'Derrota'}
                <span className="gameTurns">({game.turn} turnos)</span>
              </div>
              <span className="gameDate">Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}</span>
            </div>
            <div className="gamePlayersContainer">
              <div className="scorePlayer1">
                <Button className="gamePlayerInfo player2Info" onClick={() => handleNavigateToProfile(game.player2.nickname, navigate)}>
                  <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                </Button>
                <div className="score">{game.finalP2Score}</div>
              </div>
              <span className="gameVs">vs</span>
              <div className="scorePlayer2">
                <Button className="gamePlayerInfo player1Info" onClick={() => handleNavigateToProfile(game.player1.nickname, navigate)}>
                  {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                </Button>
                <div className="score">{game.finalP1Score}</div>
              </div>
            </div>
            <div className="gameDetailsContainer">
              <div className="gameDetail">Código de la partida: {game.code}</div>
              <div className="gameDetail">Duración: {duracion(game)} mins</div>
            </div>
          </div>
        ))}
      </div>
      <div className="watchHistoryContainer">
        <button className="watchHistoryButton" onClick={() => setShowHistoryPopup(true)}>Ver Historial</button>
      </div>
    </div>
  );
}
