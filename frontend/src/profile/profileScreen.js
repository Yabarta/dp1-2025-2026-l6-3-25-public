import React, { useEffect, useRef, useState } from "react";
import '../static/css/profile/profile.css';
import jwt_decode from "jwt-decode"; 
import { useNavigate } from "react-router-dom";
//import { getUserDetail, getUserGames, getUserStatistics } from "../api/UserEndpoints";
//import * as yup from 'yup'
import { buildInitialValues } from "./Helper";
import { Button } from "reactstrap";

export default function ProfileScreen ({user}) {
    const navigate = useNavigate();
    const imageInputRef = useRef(null);
    //getUserGames(user.id) lo que hace es obtener la lista de partidas del usuario buscando las partidas que incluyan en alguno de los dos jugadores su id
    const [userGames, setUserGames] = useState([
        {id: 1, player1: {id:1, nickname:'Player1', profilePicture: null}, player2: {id:2, nickname:'Player2', profilePicture: null}, creator: {id:1, nickname:'Player1'}, score: 3,winner: 1, createdAt: new Date('2024-04-01T10:00:00Z'), startedAt: new Date('2024-04-01T10:05:00Z'), endedAt: new Date('2024-04-01T10:25:00Z'), code: 'ABCDE', turns: 10},
        {id: 2, player1: {id:2, nickname:'Player2', profilePicture: null}, player2: {id:1, nickname:'Player1', profilePicture: null}, creator: {id:2, nickname:'Player2'}, score: 1,winner: 1, createdAt: new Date('2024-04-02T11:30:00Z'), startedAt: new Date('2024-04-02T11:32:00Z'), endedAt: new Date('2024-04-02T11:58:00Z'), code: 'FGHIJ', turns: 8},
        {id: 3, player1: {id:1, nickname:'Player1', profilePicture: null}, player2: {id:4, nickname:'Player4', profilePicture: null}, creator: {id:1, nickname:'Player1'}, score: 3,winner: 1, createdAt: new Date('2024-04-03T15:00:00Z'), startedAt: new Date('2024-04-03T15:01:00Z'), endedAt: new Date('2024-04-03T15:15:00Z'), code: 'KLMNO', turns: 12},
        {id: 4, player1: {id:1, nickname:'Player1', profilePicture: null}, player2: {id:5, nickname:'Player5', profilePicture: null}, creator: {id:1, nickname:'Player1'}, score: 4,winner: 1, createdAt: new Date('2024-04-04T18:20:00Z'), startedAt: new Date('2024-04-04T18:25:00Z'), endedAt: new Date('2024-04-04T18:45:00Z'), code: 'PQRST', turns: 9},
        {id: 5, player1: {id:3, nickname:'Player3', profilePicture: null}, player2: {id:1, nickname:'Player1', profilePicture: null}, creator: {id:3, nickname:'Player3'}, score: 5,winner: 1, createdAt: new Date('2024-04-05T20:00:00Z'), startedAt: new Date('2024-04-05T20:05:00Z'), endedAt: new Date('2024-04-05T20:30:00Z'), code: 'UVWXY', turns: 11},
        {id: 6, player1: {id:1, nickname:'Player1', profilePicture: null}, player2: {id:7, nickname:'Player7', profilePicture: null}, creator: {id:1, nickname:'Player1'}, score: 6,winner: 1, createdAt: new Date('2024-04-06T09:00:00Z'), startedAt: new Date('2024-04-06T09:03:00Z'), endedAt: new Date('2024-04-06T09:23:00Z'), code: 'ZABCD', turns: 15},
        {id: 7, player1: {id:4, nickname:'Player4', profilePicture: null}, player2: {id:1, nickname:'Player1', profilePicture: null}, creator: {id:4, nickname:'Player4'}, score: 7,winner: 1, createdAt: new Date('2024-04-07T12:10:00Z'), startedAt: new Date('2024-04-07T12:12:00Z'), endedAt: new Date('2024-04-07T12:35:00Z'), code: 'EFGHI', turns: 7},
        {id: 8, player1: {id:5, nickname:'Player5', profilePicture: null}, player2: {id:1, nickname:'Player1', profilePicture: null}, creator: {id:5, nickname:'Player5'}, score: 6,winner: 1, createdAt: new Date('2024-04-08T14:00:00Z'), startedAt: new Date('2024-04-08T14:05:00Z'), endedAt: new Date('2024-04-08T14:20:00Z'), code: 'JKLMN', turns: 13},
        {id: 9, player1: {id:1, nickname:'Player1', profilePicture: null}, player2: {id:10, nickname:'Player10', profilePicture: null}, creator: {id:1, nickname:'Player1'}, score: 5,winner: 1, createdAt: new Date('2024-04-09T16:45:00Z'), startedAt: new Date('2024-04-09T16:50:00Z'), endedAt: new Date('2024-04-09T17:10:00Z'), code: 'OPQRS', turns: 10},
        {id: 10, player1: {id:1, nickname:'Player1', profilePicture: null}, player2: {id:11, nickname:'Player11', profilePicture: null}, creator: {id:1, nickname:'Player1'}, score: 5,winner: 1, createdAt: new Date('2024-04-10T19:00:00Z'), startedAt: new Date('2024-04-10T19:02:00Z'), endedAt: new Date('2024-04-10T19:28:00Z'), code: 'TUVWX', turns: 14},
    ]);
    const [userStats, setUserStats] = useState({});
    const [userData, setUserData] = useState({
        id: 1,
        username: 'Player1',
        createdAt: new Date().toISOString(),
        profilePicture: null
    });
    const [profilePic, setProfilePic] = useState(userData.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png");
    const [showHistoryPopup, setShowHistoryPopup] = useState(false);

    const [initialUserValues, setInitialUserValues] = useState({ name: null, email: null, profilePicture: null })
//   const validationSchema = yup.object().shape({
//     name: yup
//       .string()
//       .max(255, 'Name too long')
//       .required('Name is required'),
//     email: yup
//       .string()
//       .nullable()
//       .email('Please enter a valid email'),
//     profilePicture: yup
//       .string()
//       .nullable()
//       .url('Please enter a valid URL')
//   })
  
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
    //     alert('Profile successfully updated');
    //     navigate('/profileScreen', { dirty: true })
    // } catch (error) {
    //   console.error('Error:', error);
    //   alert(error.message);
    // }
    // };

    return (
        <div className="profileContainer">
            <div className="left">
                <div className="profileHeader">
                    <span>{userData.username} || Pepito </span>
                    <span>🇪🇸</span>
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
                            <span className="statLabel">Joined</span>
                            <span className="statValue">{new Date(userData.createdAt).toLocaleDateString()}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Play Time</span>
                            <span className="statValue">{userStats.timePlayed || 0} hours</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Online Games</span>
                            <span className="statValue">{userGames.length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Of which wins</span>
                            <span className="statValue">{userStats.gamesWon || userGames.filter(isWinner).length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Losses</span>
                            <span className="statValue">{userStats.gamesLost || userGames.filter((game) => !isWinner(game)).length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Sarcinas</span>
                            <span className="statValue">{userStats.sarcinasCol || 0}</span>
                        </div>
                    </div>
                    <button className="editProfileButton" onClick={() => navigate('/editProfileScreen')}>Editar Perfil</button>
                </div>
            </div>
            <div className="right">
                <div className="bg">
                    <h1 className="title">Partidas Recientes</h1>
                    <div className="recentGamesContainer">
                        {userGames.slice(0, 3).map(game => {
                             return (
                                <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
                                        <div style={{ fontSize: '1.2rem' }}>
                                            {isWinner(game) ? "Victoria" : "Derrota"}
                                            <span style={{ margin: '0.5rem', fontSize: '0.8rem' }}>
                                                ({game.turns} turnos)
                                            </span>
                                        </div>
                                        <span style={{ fontSize: '1.2rem' }}>
                                            Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', marginTop: '0.5rem', justifyContent: 'space-between', width: '100%',fontSize: '1.2rem' }}>
                                       <div style={{ display: 'flex', alignItems: 'center',marginLeft: '15rem' }}>
                                            <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} style={{ width: '3rem', height: '3rem', borderRadius: '50%', margin: '0.5rem' }} /> {game.player2.nickname}
                                        </div>
                                        <span style={{ margin: '0.5rem' }}>vs</span>
                                        <div style={{ display: 'flex', alignItems: 'center',marginRight: '15rem'  }}>
                                            {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} style={{ width: '3rem', height: '3rem', borderRadius: '50%', margin: '0.5rem' }} />
                                        </div>
                                    </div>
                                    <div style={{ marginTop: '0.5rem', display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                                        <div style={{ fontSize: '0.8rem', margin: '0.5rem', borderLeft: '1px solid #444' }}>
                                            Código de la partida: {game.code}
                                        </div>
                                        <div style={{ fontSize: '0.8rem', margin: '0.5rem', borderLeft: '1px solid #444' }}>
                                            Puntuación: {game.score}
                                        </div>
                                        <div style={{ fontSize: '0.8rem', margin: '0.5rem', borderLeft: '1px solid #444' }}>
                                            Duracion: {duracion(game)} mins
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                    <div style={{alignSelf: 'flex-start', marginTop: '1rem'}}>
                        <button className="watchHistoryButton" onClick={() => setShowHistoryPopup(true)}>
                            Ver Historial
                        </button>
                    </div>
                </div>
                <div className="bg">
                    <h1 className="title">Logros</h1>
                    <h4>Completado completedAchievements/totalAchievements</h4>
                    <div className="mainStatContainer">
                        <div style={{display: 'flex', alignItems: 'center', flexDirection: 'row'}}>
                            <img src="https://media.tenor.com/On7kvXhzml4AAAAj/loading-gif.gif" alt="loading" style={{width: '3rem', height: '3rem', marginRight: '1rem'}} />
                            Logro 1
                        </div>
                        <div className="mainStatContainerSubText">
                        Descripcion logro 1
                        </div>
                        <div style={{display: 'flex', alignItems: 'center', flexDirection: 'row'}}>
                            <img src="https://media.tenor.com/On7kvXhzml4AAAAj/loading-gif.gif" alt="loading" style={{width: '3rem', height: '3rem', marginRight: '1rem'}} />
                            Logro 2
                        </div>
                        <div className="mainStatContainerSubText">
                        Descripcion logro 2
                        </div>
                        <div style={{display: 'flex', alignItems: 'center', flexDirection: 'row'}}>
                            <img src="https://media.tenor.com/On7kvXhzml4AAAAj/loading-gif.gif" alt="loading" style={{width: '3rem', height: '3rem', marginRight: '1rem'}} />
                            Logro 3
                        </div>
                        <div className="mainStatContainerSubText">
                        Descripcion logro 3
                        </div>
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
                                    <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%', alignItems: 'center' }}>
                                        <div style={{ fontSize: '1.2rem' }}>
                                            {isWinner(game) ? "Victoria" : "Derrota"}
                                            <span style={{ margin: '0.5rem', fontSize: '0.8rem' }}>
                                                ({game.turns} turnos)
                                            </span>
                                        </div>
                                        <span style={{ fontSize: '1.2rem' }}>
                                            Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', marginTop: '0.5rem', justifyContent: 'space-between', width: '100%',fontSize: '1.2rem' }}>
                                       <div style={{ display: 'flex', alignItems: 'center' }}>
                                            <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} style={{ width: '3rem', height: '3rem', borderRadius: '50%', margin: '0.5rem' }} /> {game.player2.nickname}
                                        </div>
                                        <span style={{ margin: '0.5rem' }}>vs</span>
                                        <div style={{ display: 'flex', alignItems: 'center' }}>
                                            {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} style={{ width: '3rem', height: '3rem', borderRadius: '50%', margin: '0.5rem' }} />
                                        </div>
                                    </div>
                                    <div style={{ marginTop: '0.5rem', display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', width: '100%' }}>
                                        <div style={{ fontSize: '0.8rem', margin: '0.5rem', borderLeft: '1px solid #444' }}>
                                            Código de la partida: {game.code}
                                        </div>
                                        <div style={{ fontSize: '0.8rem', margin: '0.5rem', borderLeft: '1px solid #444' }}>
                                            Puntuación: {game.score}
                                        </div>
                                        <div style={{ fontSize: '0.8rem', margin: '0.5rem', borderLeft: '1px solid #444' }}>
                                            Duracion: {duracion(game)} mins
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
        </div>
        );
}