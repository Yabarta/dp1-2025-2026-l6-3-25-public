import React, { useEffect, useRef, useState } from "react";
import jwt_decode from "jwt-decode";
import tokenService from "../services/token.service";
import '../static/css/profile/profile.css';
import { useNavigate } from "react-router-dom";
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';

export default function ProfileScreen() {
    const jwt = tokenService.getLocalAccessToken();
    const navigate = useNavigate();
    const imageInputRef = useRef(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [username, setUsername] = useState("");

    
    const [players, setPlayers] =useFetchState(
        [],
        `/api/v1/players`,
        jwt,
        setMessage,
        setVisible,
    );

    const [playerData, setPlayerData] = useState({});
    const [games, setGames] = useFetchState(
        [],
        `/api/v1/matches`,
        jwt,
        setMessage,
        setVisible,
    );
    const [userGames, setUserGames] = useState([]);
    const [userStats, setUserStats] = useState({});
    
    //No puedo probar los logros bien porque el backend no los tiene implementados todavía
    const [Achievements, setAchievements] = useFetchState(
        [{ id: 1, name: "Primera Victoria", description: "Gana tu primera partida.", icon: "https://example.com/icons/first_win.png", StatisticValueName: "gamesWon", value: 1 },
        { id: 2, name: "Veterano", description: "Juega 50 partidas.", icon: "https://example.com/icons/veteran.png", StatisticValueName: "gamesPlayed", value: 50 },
        ],
        //`/api/v1/players/{playerData.id}/achievements`
        "",
        jwt,
        setMessage,
        setVisible,
    );
    const [UserAchievements, setUserAchievements] = useFetchState(
        [{ id: 1, name: "Primera Victoria", description: "Gana tu primera partida.", icon: "https://example.com/icons/first_win.png", StatisticValueName: "gamesWon", value: 1 },],
        //`/api/v1/achievements`
        "",
        jwt,
        setMessage,
        setVisible,
    );

    useEffect(() => {
        const userName = jwt_decode(jwt).sub;
        setUsername(userName);
        const player = players.filter((player) => player.user.username === userName)[0] || {};
        setPlayerData(player);
        const userGamesFiltered = games.filter((game) => game.player1.id === player.id || game.player2.id === player.id);
        setUserGames(userGamesFiltered);
        setUserStats(
            { gamesPlayed: userGamesFiltered.length, gamesWon: userGamesFiltered.filter((game) => {
        const isPlayer1 = game.player1.id === player.id;
        return (game.winner === 1 && isPlayer1) || (game.winner === 2 && !isPlayer1)}).length, hoursPlayed: userGamesFiltered.reduce((total, game) => total + duracion(game), 0) / 60 }
        );
        }, [players, games, jwt]);

    


    const modal = getErrorModal(setVisible, visible, message);

    const getPlayerProfilePic = (player) => {
        return player.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png";
    };
    const duracion = (game) => {
        const createdAt = new Date(game.createdAt);
        const endedAt = new Date(game.endedAt);
        return Math.floor((endedAt.getTime() - createdAt.getTime()) / 60000); // Duración en minutos
    }
    const isWinner = (game) => {
        const isPlayer1 = game.player1.id === playerData.id;
        return (game.winner === 1 && isPlayer1) || (game.winner === 2 && !isPlayer1);
    };
    
    const achievementProgress = (achievement, stats) => {
        if (!stats) return '0';
        const progress = Math.round(userStats[achievement.StatisticValueName]);
        return `${progress >= achievement.value ? achievement.value : progress}/${achievement.value}`;
    };
    


    const [profilePic, setProfilePic] = useState(playerData.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png");
    const [showHistoryPopup, setShowHistoryPopup] = useState(false);

    const handleChangeProfilePicture = () => {
        imageInputRef.current.click();
    };

    const handleFileChange = async (event) => {
        const image = event.target.files[0];
        if (image) {
            alert(`Archivo seleccionado: ${image.name}. Aún no está implementado xd.`);
            // Aquí se implementaría la lógica para subir la imagen al servidor y actualizar la foto de perfil del usuario
            // Seria de la siguiente manera(Obviamente llamando al backend con sus funciones correspondientes)
            //     const formData = new FormData();
            //     formData.append('profilePicture', image);
            //     try {
            //         const response = await updateUserProfilePicture(playerData.id, formData);
            //         const updatedUser = await response.json();
            //         setProfilePic(updatedUser.profilePicture);
            //         alert('Imagen de perfil actualizada con éxito.');

            //     } catch (error) {
            //         console.error('Error:', error);
            //         alert(error.message);
            //     }
        }
    };

    const validationSchema = Yup.object().shape({
        nickname: Yup.string()
            .max(255, 'El nombre de usuario es demasiado largo')
            .required('El nombre de usuario es requerido'),
        email: Yup.string()
            .email('Por favor, introduce un correo electrónico válido')
            .required('El correo electrónico es requerido'),
    });

    const handleEditSubmit = (values) => {
        const updatedPlayer = { ...playerData, ...values };
        setPlayerData(updatedPlayer);
        setShowEditPopup(false);

        fetch("/api/v1/players/" + (updatedPlayer.id || playerData.id), {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedPlayer),
        })
            .then((response) => {
                if (!response.ok) {
                    return response.json().then((json) => {
                        const msg = (json && json.message) ? json.message : `Error ${response.status}`;
                        setMessage(msg);
                        setVisible(true);
                        throw new Error(msg);
                    }).catch(() => {
                        const msg = `Error ${response.status}`;
                        setMessage(msg);
                        setVisible(true);
                        throw new Error(msg);
                    });
                }
                return response.json().catch(() => ({}));
            })
            .then((json) => {
                if (json && json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    setPlayers(prev => prev.map(p => p.id === updatedPlayer.id ? updatedPlayer : p));
                    window.location.reload();
                }
            })
            .catch((err) => {
                const msg = err && err.message ? err.message : 'Error updating profile';
                setMessage(msg);
                setVisible(true);
            });
    };
    return (
        <div className="profileContainer">{modal}
            <div className="left">
                <div>
                    <div className="profileHeader">
                        <span style={{ marginLeft: '1rem', marginTop: '0.5rem' }}>{playerData.nickname}</span>
                        <span onClick={() => setShowEditPopup(true)} className="editIcon">✏️</span>
                    </div>
                    <div className="profileHeaderEmail">{playerData.email}</div>
                </div>
                <div className="bg">
                    <img src={profilePic}
                        onClick={handleChangeProfilePicture}
                        alt="provisional"
                        className="profilePicture" />
                    <input
                        type="file"
                        ref={imageInputRef}
                        onChange={handleFileChange}
                        style={{ display: 'none' }}
                        accept="image/*"
                    />
                    <div className="mainStatContainer">
                        <div className="statItem">
                            <span className="statLabel">Fecha de Creación</span>
                            <span className="statValue">{playerData.createdAt? new Date(playerData.createdAt).toLocaleDateString() : new Date().toLocaleDateString()}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Tiempo de Juego</span>
                            <span className="statValue">{Math.floor(userStats.hoursPlayed || 0) || 0} horas y {Math.round((userStats.hoursPlayed - Math.floor(userStats.hoursPlayed)) * 60) || 0} minutos</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Partidas Online</span>
                            <span className="statValue">{userGames.length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Victorias</span>
                            <span className="statValue">{userStats.gamesWon || userGames.filter(isWinner).length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Derrotas</span>
                            <span className="statValue">{userStats.gamesLost || userGames.filter((game) => !isWinner(game)).length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Sarcinas</span>
                            <span className="statValue">{userStats.sarcinasCol || 0}</span>
                        </div>
                    </div>
                    {/* El botón de editar perfil se ha movido al lado del nombre de usuario */}
                </div>
            </div>
            <div className="right">
                <div className="bg">
                    <h1 className="title">Partidas Recientes</h1>
                    <div className="recentGamesContainer">
                        {userGames.slice(0, 3).map(game => {
                            return (
                                <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                                    <div className="gameHeader">
                                        <div className="gameResult">
                                            {isWinner(game) ? "Victoria" : "Derrota"}
                                            <span className="gameTurns">
                                                ({game.turn} turnos)
                                            </span>
                                        </div>
                                        <span className="gameDate">
                                            Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                    <div className="gamePlayersContainer">
                                        <div className="scorePlayer1">
                                            <div className="gamePlayerInfo player2Info">
                                                <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                                            </div>
                                            <div className="score">
                                                {game.finalP2Score}
                                            </div>
                                        </div>
                                        <span className="gameVs">vs</span>
                                        <div className="scorePlayer2">
                                            <div className="gamePlayerInfo player1Info">
                                                {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                                            </div>
                                            <div className="score">
                                                {game.finalP1Score}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="gameDetailsContainer">
                                        <div className="gameDetail">
                                            Código de la partida: {game.code}
                                        </div>
                                        <div className="gameDetail">
                                            Duración: {duracion(game)} mins
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                    <div className="watchHistoryContainer">
                        <button className="watchHistoryButton" onClick={() => setShowHistoryPopup(true)}>
                            Ver Historial
                        </button>
                    </div>
                </div>
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
                                        <p className="achievementProgress">
                                            {achievementProgress(achievement, userStats)}
                                        </p>
                                    </div>
                                    <div className="achievementInfo">
                                        <div>
                                            <p className="achievementDescriptionContainer achievementDescription">
                                                {achievement.description}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
            {showHistoryPopup && (
                <div className="popupOverlay">
                    <div className="popupContent">
                        <h2 className="title">Historial de Partidas</h2>
                        <button onClick={() => setShowHistoryPopup(false)} className="closePopupButton">X</button>
                        <div className="gamesList">
                            {userGames.length > 0 ? (
                                userGames.map(game => {
                                    return (
                                        <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                                            <div className="gameHeader">
                                                <div className="gameResult">
                                                    {isWinner(game) ? "Victoria" : "Derrota"}
                                                    <span className="gameTurns">
                                                        ({game.turns} turnos)
                                                    </span>
                                                </div>
                                                <span className="gameDate">
                                                    Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                                </span>
                                            </div>
                                            <div className="gamePlayersContainer">
                                                <div className="gamePlayerInfo">
                                                    <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                                                </div>
                                                <span className="gameVs">vs</span>
                                                <div className="gamePlayerInfo">
                                                    {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                                                </div>
                                            </div>
                                            <div className="gameDetailsContainer">
                                                <div className="gameDetail">
                                                    Código de la partida: {game.code}
                                                </div>
                                                <div className="gameDetail">
                                                    Puntuación: {game.score}
                                                </div>
                                                <div className="gameDetail">
                                                    Duración: {duracion(game)} mins
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })
                            ) : (
                                <p>No hay partidas para mostrar.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
            {showEditPopup && (
                <div className="popupOverlay">
                    <div className="popupContent">
                        <h2 className="title">Editar Perfil</h2>
                        <button onClick={() => setShowEditPopup(false)} className="closePopupButton">X</button>
                        <Formik
                            initialValues={{
                                nickname: playerData.nickname,
                                email: playerData.email,
                            }}
                            validationSchema={validationSchema}
                            onSubmit={handleEditSubmit}
                        >
                            {({ isSubmitting }) => (
                                <Form>
                                    <div className="formGroup">
                                        <label htmlFor="nickname">Nombre de usuario</label>
                                        <Field name="nickname" type="text" className="formControl" />
                                        <ErrorMessage name="nickname" component="div" className="error" />
                                    </div>
                                    <div className="formGroup">
                                        <label htmlFor="email">Email</label>
                                        <Field name="email" type="email" className="formControl" />
                                        <ErrorMessage name="email" component="div" className="error" />
                                    </div>
                                    <div className="formButtons">
                                        <button type="submit" className="editProfileButton" disabled={isSubmitting}>
                                            Guardar Cambios
                                        </button>
                                        <button type="button" className="watchHistoryButton" onClick={() => setShowEditPopup(false)}>
                                            Cancelar
                                        </button>
                                    </div>
                                </Form>
                            )}
                        </Formik>
                    </div>
                </div>
            )}
        </div>
    );
}