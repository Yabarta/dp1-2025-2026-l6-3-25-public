import React from 'react';
import { Button } from 'reactstrap';

export default function HistoryPopup({ showHistoryPopup, setShowHistoryPopup, userGames = [], isWinner, getPlayerProfilePic, handleNavigateToProfile, duracion, navigate }) {
  if (!showHistoryPopup) return null;
  const sortedGames = [...(userGames || [])].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  return (
    <div className="popupOverlay">
      <div className="popupContent">
        <h2 className="title">Historial de Partidas</h2>
        <button onClick={() => setShowHistoryPopup(false)} className="closePopupButton">X</button>
        <div className="gamesList">
          {sortedGames.length > 0 ? sortedGames.map(game => (
            <div key={game.id} className={isWinner(game) ? 'gameWinBg' : 'gameLoseBg'}>
              <div className="gameHeader">
                <div className="gameResult">
                  {isWinner(game) ? 'Victoria' : 'Derrota'}
                  <span className="gameTurns">({game.turn} turnos)</span>
                </div>
                <span className="gameDate">Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}</span>
              </div>
              <div className="gamePlayersContainer">
                <Button className="gamePlayerInfo player2Info" onClick={() => { setShowHistoryPopup(false); handleNavigateToProfile(game.player2.nickname, navigate); }}>
                  <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" />
                  <span style={{ marginLeft: '8px' }}>{game.player2.nickname}</span>
                  <span className="gamePlayerScore" style={{ color: 'var(--petris-green-strong)', fontWeight: 800, marginLeft: '60px' }}>{game.player2Score}</span>
                </Button>
                <span className="gameVs">vs</span>
                <Button className="gamePlayerInfo player1Info" onClick={() => { setShowHistoryPopup(false); handleNavigateToProfile(game.player1.nickname, navigate); }}>
                  <span className="gamePlayerScore" style={{ color: 'var(--petris-green-strong)', fontWeight: 800, marginRight: '60px' }}>{game.player1Score}</span>
                  <span style={{ marginRight: '8px' }}>{game.player1.nickname}</span>
                  <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                </Button>
              </div>
              <div className="gameDetailsContainer">
                <div className="gameDetail">Código de la partida: {game.code}</div>
                <div className="gameDetail">Duración: {duracion(game)} minutos</div>
              </div>
            </div>
          )) : <p>No hay partidas para mostrar.</p>}
        </div>
      </div>
    </div>
  );
}
