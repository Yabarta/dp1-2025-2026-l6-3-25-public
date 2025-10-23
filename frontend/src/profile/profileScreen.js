import React, { useEffect, useRef, useState } from "react";
import '../static/css/profile/profile.css';
import jwt_decode from "jwt-decode"; 
import { useNavigate } from "react-router-dom";
//import { getUserDetail, getUserGames, getUserStatistics } from "../api/UserEndpoints";
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { buildInitialValues } from "./Helper";
import { Button } from "reactstrap";

export default function ProfileScreen ({user}) {
    const navigate = useNavigate();
    const imageInputRef = useRef(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [userData, setUserData] = useState({ //jwt_decode(user)
        id: 1,
        username: 'Jugador1',
        email: 'jugador1@example.com',
        createdAt: new Date().toISOString(),
        profilePicture: null
    });
    const getPlayerProfilePic = (player) => {
        return player.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png";
    };
    const duracion = (game) => {
        return (game.endedAt.getTime() - game.createdAt.getTime()) / 60000; // Duración en minutos
    }
    const isWinner = (game) => {
        const isPlayer1 = game.player1.id === userData.id;
        return (game.winner === 1 && isPlayer1) || (game.winner === 2 && !isPlayer1);
    };
    const completedAchievements = (achievements, stats) => {
        if (!stats) return 0;
        return achievements.filter(achievement => stats[achievement.StatisticValueName] >= achievement.value).length;
    }
    const achievementProgress = (achievement, stats) => {
        if (!stats) return '0';
        const progress = Math.round(userStats[achievement.StatisticValueName]);
        return `${progress >= achievement.value ? achievement.value : progress}/${achievement.value}`;
    };
    //getUserGames(user.id) lo que hace es obtener la lista de partidas del usuario buscando las partidas que incluyan en alguno de los dos jugadores su id
    const [userGames, setUserGames] = useState([
        {id: 1, player1: {id:1, nickname:'Jugador1', profilePicture: null}, player2: {id:2, nickname:'Jugador2', profilePicture: null}, creator: {id:1, nickname:'Jugador1'}, score: 3,winner: 1, createdAt: new Date('2024-04-01T10:00:00Z'), startedAt: new Date('2024-04-01T10:05:00Z'), endedAt: new Date('2024-04-01T10:25:00Z'), code: 'ABCDE', turns: 10},
        {id: 2, player1: {id:2, nickname:'Jugador2', profilePicture: null}, player2: {id:1, nickname:'Jugador1', profilePicture: null}, creator: {id:2, nickname:'Jugador2'}, score: 1,winner: 1, createdAt: new Date('2024-04-02T11:30:00Z'), startedAt: new Date('2024-04-02T11:32:00Z'), endedAt: new Date('2024-04-02T11:58:00Z'), code: 'FGHIJ', turns: 8},
        {id: 3, player1: {id:1, nickname:'Jugador1', profilePicture: null}, player2: {id:4, nickname:'Jugador4', profilePicture: null}, creator: {id:1, nickname:'Jugador1'}, score: 3,winner: 1, createdAt: new Date('2024-04-03T15:00:00Z'), startedAt: new Date('2024-04-03T15:01:00Z'), endedAt: new Date('2024-04-03T15:15:00Z'), code: 'KLMNO', turns: 12},
        {id: 4, player1: {id:1, nickname:'Jugador1', profilePicture: null}, player2: {id:5, nickname:'Jugador5', profilePicture: null}, creator: {id:1, nickname:'Jugador1'}, score: 4,winner: 1, createdAt: new Date('2024-04-04T18:20:00Z'), startedAt: new Date('2024-04-04T18:25:00Z'), endedAt: new Date('2024-04-04T18:45:00Z'), code: 'PQRST', turns: 9},
        {id: 5, player1: {id:3, nickname:'Jugador3', profilePicture: null}, player2: {id:1, nickname:'Jugador1', profilePicture: null}, creator: {id:3, nickname:'Jugador3'}, score: 5,winner: 1, createdAt: new Date('2024-04-05T20:00:00Z'), startedAt: new Date('2024-04-05T20:05:00Z'), endedAt: new Date('2024-04-05T20:30:00Z'), code: 'UVWXY', turns: 11},
        {id: 6, player1: {id:1, nickname:'Jugador1', profilePicture: null}, player2: {id:7, nickname:'Jugador7', profilePicture: null}, creator: {id:1, nickname:'Jugador1'}, score: 6,winner: 1, createdAt: new Date('2024-04-06T09:00:00Z'), startedAt: new Date('2024-04-06T09:03:00Z'), endedAt: new Date('2024-04-06T09:23:00Z'), code: 'ZABCD', turns: 15},
        {id: 7, player1: {id:4, nickname:'Jugador4', profilePicture: null}, player2: {id:1, nickname:'Jugador1', profilePicture: null}, creator: {id:4, nickname:'Jugador4'}, score: 7,winner: 1, createdAt: new Date('2024-04-07T12:10:00Z'), startedAt: new Date('2024-04-07T12:12:00Z'), endedAt: new Date('2024-04-07T12:35:00Z'), code: 'EFGHI', turns: 7},
        {id: 8, player1: {id:5, nickname:'Jugador5', profilePicture: null}, player2: {id:1, nickname:'Jugador1', profilePicture: null}, creator: {id:5, nickname:'Jugador5'}, score: 6,winner: 1, createdAt: new Date('2024-04-08T14:00:00Z'), startedAt: new Date('2024-04-08T14:05:00Z'), endedAt: new Date('2024-04-08T14:20:00Z'), code: 'JKLMN', turns: 13},
        {id: 9, player1: {id:1, nickname:'Jugador1', profilePicture: null}, player2: {id:10, nickname:'Jugador10', profilePicture: null}, creator: {id:1, nickname:'Jugador1'}, score: 5,winner: 1, createdAt: new Date('2024-04-09T16:45:00Z'), startedAt: new Date('2024-04-09T16:50:00Z'), endedAt: new Date('2024-04-09T17:10:00Z'), code: 'OPQRS', turns: 10},
        {id: 10, player1: {id:1, nickname:'Jugador1', profilePicture: null}, player2: {id:11, nickname:'Jugador11', profilePicture: null}, creator: {id:1, nickname:'Jugador1'}, score: 5,winner: 1, createdAt: new Date('2024-04-10T19:00:00Z'), startedAt: new Date('2024-04-10T19:02:00Z'), endedAt: new Date('2024-04-10T19:28:00Z'), code: 'TUVWX', turns: 14},
    ]);
     const [userStats, setUserStats] = useState(
        { gamesPlayed: userGames.length, gamesWon: userGames.filter(isWinner).length, hoursPlayed: userGames.reduce((total, game) => total + duracion(game), 0) / 60 }
    );
    const [UserAchievements, setUserAchievements] = useState([
        {id: 1, name: 'Primera Victoria', description: 'Gana tu primera partida', icon: 'https://example.com/first_win.png', value:1, StatisticValueName: 'gamesWon' },
        {id: 2, name: 'Centurión', description: 'Juega 100 partidas', icon: 'https://example.com/centurion.png', value:100, StatisticValueName: 'gamesPlayed' },
        {id: 3, name: 'Maratoniano', description: 'Juega durante 10 horas', icon: 'https://example.com/marathoner.png', value:10, StatisticValueName: 'hoursPlayed' },
    ]);
   
    
    const [profilePic, setProfilePic] = useState(userData.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png");
    const [showHistoryPopup, setShowHistoryPopup] = useState(false);

    const [initialUserValues, setInitialUserValues] = useState({ name: null, email: null, profilePicture: null })
  
//     useEffect(() => {
//     async function fetchUserData () {
//       try {
//         const fetchedUser = await getUserDetail(userData.id)
//         const fetchedUserGames = await getUserGames(userData.id)
//         const fetchedUserStats = await getUserStatistics(userData.id)
//         setUserGames(fetchedUserGames)
//         setUserData(fetchedUser)
//         setUserStats(fetchedUserStats)
//         const initialValues = buildInitialValues(fetchedUser, initialUserValues)
//         setInitialUserValues(initialValues)
//       } catch (error) {
//         alert(`There was an error while retrieving user details (id ${userData.id}). ${error}`)
//         }
//     }
//     fetchUserData()
//   }, [userData, initialUserValues]);

    

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
            //         const response = await updateUserProfilePicture(userData.id, formData);
            //         const updatedUser = await response.json();
            //         setProfilePic(updatedUser.profilePicture);
            //         alert('Imagen de perfil actualizada con éxito.');

            //     } catch (error) {
            //         console.error('Error:', error);
            //         alert(error.message);
            //     }
             }
    };
    // const updateUserProfile = async (values) => {
    // try {
    //     const updatedUser = await updateUser(values)
    //     setUserData(updatedUser)
    //     alert('Perfil actualizado correctamente');
    //     navigate('/profileScreen', { dirty: true })
    // } catch (error) {
    //   console.error('Error:', error);
    //   alert(error.message);
    // }
    // };

    const validationSchema = Yup.object().shape({
        username: Yup.string()
            .max(255, 'El nombre de usuario es demasiado largo')
            .required('El nombre de usuario es requerido'),
        email: Yup.string()
            .email('Por favor, introduce un correo electrónico válido')
            .required('El correo electrónico es requerido'),
    });

    const handleEditSubmit = (values) => {
        setUserData(prevData => ({ ...prevData, ...values }));
        setShowEditPopup(false);
        alert('Perfil actualizado con éxito (simulación).');
    };
    //Despues de cambiar los valores hasta que no funcione el backend no se podra actualizar realmente la informacion del usuario
    return (
        <div className="profileContainer">
            <div className="left">
                <div>
                <div className="profileHeader">
                    <span style={{marginLeft: '1rem',marginTop: '0.5rem'}}>{userData.username}</span>
                    <span onClick={() => setShowEditPopup(true)} className="editIcon">✏️</span>
                </div>
                <div className="profileHeaderEmail">{userData.email}</div>
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
                            <span className="statValue">{new Date(userData.createdAt).toLocaleDateString()}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Tiempo de Juego</span>
                            <span className="statValue">{Math.round(userStats.hoursPlayed || 0) || 0} horas y {Math.round((userStats.hoursPlayed - Math.floor(userStats.hoursPlayed || 0)) * 60) || 0} minutos</span>
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
                                                ({game.turns} turnos)
                                            </span>
                                        </div>
                                        <span className="gameDate">
                                            Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                    <div className="gamePlayersContainer">
                                       <div className="gamePlayerInfo player2Info">
                                            <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                                        </div>
                                        <span className="gameVs">vs</span>
                                        <div className="gamePlayerInfo player1Info">
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
                    <h4>Completado {completedAchievements(UserAchievements, userStats)}/{UserAchievements.length}</h4>
                    <div className="mainStatContainer">
                        {UserAchievements.map(achievement => {
                            const isCompleted = userStats[achievement.StatisticValueName.trim()] >= achievement.value;
                            return (
                                <div key={achievement.id} className={`achievement ${isCompleted ? 'completed' : ''}`}>
                                    <div className="achievementHeader">
                                        <img src={achievement.icon} alt={achievement.name} className="achievementIcon"/>
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
                                username: userData.username,
                                email: userData.email,
                            }}
                            validationSchema={validationSchema}
                            onSubmit={handleEditSubmit}
                        >
                            {({ isSubmitting }) => (
                                <Form>
                                    <div className="formGroup">
                                        <label htmlFor="username">Nombre de usuario</label>
                                        <Field name="username" type="text" className="formControl" />
                                        <ErrorMessage name="username" component="div" className="error" />
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