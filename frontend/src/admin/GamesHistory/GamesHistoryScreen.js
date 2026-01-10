import useFetchState  from '../../util/useFetchState';
import tokenService from "../../services/token.service";
import { Button } from 'reactstrap';
import { useState, useCallback } from 'react';
import { useNavigate } from "react-router-dom";
import profileScreen, {navigateToProfile} from '../../profile/profileScreen';
import bacteria from '../../static/images/bacteria.png';

const DEFAULT_PROFILE_PIC = bacteria;

export default function GamesHistoryScreen() {

const navigate = useNavigate();

const [message, setMessage] = useState(null);
const [visible, setVisible] = useState(false);
const jwt = tokenService.getLocalAccessToken();
const getPlayerProfilePic = useCallback((player) => player.profilePicture || DEFAULT_PROFILE_PIC, []);
const duracion = useCallback((game) => {
        const createdAt = new Date(game.createdAt);
        const endedAt = new Date(game.endedAt);
        return Math.floor((endedAt.getTime() - createdAt.getTime()) / 60000);
    }, []);

const [games, , gamesLoading] = useFetchState([], `/api/v1/matches`, jwt, setMessage, setVisible);
const endedGames = games.filter(game => game.endedAt != null);
console.log(games);

return (  
<div>
    <div className="bg">
      <h1 className="title">Historial de Partidas</h1>
      <div className="recentGamesContainer">
        {gamesLoading ? (<div>Cargando...</div>) : (
            <ul>
        {endedGames.map((game) => (
        <li key={game.id} >
          <div key={game.id} className={'GameCard'}>
            <div className="gameHeader">
              <div className="gameResult">
                {'Ganador:' + (game.winner===game.player1.id ? game.player1.nickname : game.player2.nickname)}
                <span className="gameTurns">({game.turn} turnos)</span>
              </div>
              <span className="gameDate">Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}</span>
            </div>
            <div className="gamePlayersContainer">
              <div className="scorePlayer1">
                <Button className="gamePlayerInfo player2Info" onClick={() => navigateToProfile(game.player2.nickname, navigate)}>
                  <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                </Button>
                <div className="score">{game.finalP2Score}</div>
              </div>
              <span className="gameVs">vs</span>
              <div className="scorePlayer2">
                <Button className="gamePlayerInfo player1Info" onClick={() => navigateToProfile(game.player1.nickname, navigate)}>
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
          </li>
        ))}
          </ul>)}
      </div> 
    </div>
</div>

);
}