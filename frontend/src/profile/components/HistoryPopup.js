import React from 'react';
import { Button } from 'reactstrap';

export default function HistoryPopup({ showHistoryPopup, setShowHistoryPopup, userGames = [], isWinner, getPlayerProfilePic, handleNavigateToProfile, duracion }) {
  if (!showHistoryPopup) return null;
  return (
    <div className="popupOverlay">
      <div className="popupContent">
        <h2 className="title">Historial de Partidas</h2>
        <button onClick={() => setShowHistoryPopup(false)} className="closePopupButton">X</button>
        <div className="gamesList">
          {userGames.length > 0 ? userGames.map(game => (
            <div key={game.id} className={isWinner(game) ? 'gameWinBg' : 'gameLoseBg'}>
              <div className="gameHeader">
                <div className="gameResult">
                  {isWinner(game) ? 'Victoria' : 'Derrota'}
                  <span className="gameTurns">({game.turns} turnos)</span>
                </div>
                <span className="gameDate">Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}</span>
              </div>
              <div className="gamePlayersContainer">
                <Button className="gamePlayerInfo" onClick={() => { setShowHistoryPopup(false); handleNavigateToProfile(game.player2.nickname); }}>
                  <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                </Button>
                <span className="gameVs">vs</span>
                <Button className="gamePlayerInfo" onClick={() => { setShowHistoryPopup(false); handleNavigateToProfile(game.player1.nickname); }}>
                  {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                </Button>
              </div>
              <div className="gameDetailsContainer">
                <div className="gameDetail">Código de la partida: {game.code}</div>
                <div className="gameDetail">Puntuación: {game.score}</div>
                <div className="gameDetail">Duración: {duracion(game)} mins</div>
              </div>
            </div>
          )) : <p>No hay partidas para mostrar.</p>}
        </div>
      </div>
    </div>
  );
}
