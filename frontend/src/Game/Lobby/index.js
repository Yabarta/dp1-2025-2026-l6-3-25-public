import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useNavigate } from 'react-router-dom';

const Lobby = () => {
    const [lobby, setLobby] = useState(null);
    const [players, setPlayers] = useState([]);
    const [joinCode, setJoinCode] = useState('');
    const [stompClient, setStompClient] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (lobby && !stompClient) {
            const socket = new SockJS('/ws');
            const client = new Client({
                webSocketFactory: () => socket,
                onConnect: () => {
                    client.subscribe(`/topic/sala/${lobby.codigoDeUnion}`, message => {
                        setPlayers(JSON.parse(message.body));
                    });
                },
            });
            client.activate();
            setStompClient(client);
        }

        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
    }, [lobby, stompClient]);

    const createLobby = async () => {
        try {
            const response = await api.post('/api/salas');
            const newLobby = response.data;
            navigate(`/lobby/${newLobby.codigoDeUnion}`);
        } catch (error) {
            console.error("Error creating lobby", error);
        }
    };

    const joinLobby = async () => {
        try {
            const response = await api.post(`/api/salas/${joinCode}/unirse`, "newPlayer"); // Replace "newPlayer" with actual player name
            const joinedLobby = response.data;
            navigate(`/lobby/${joinedLobby.codigoDeUnion}`);
        } catch (error) {
            console.error("Error joining lobby", error);
        }
    };

    return (
        <div>
            <h1>Lobby</h1>
            <div>
                <button onClick={createLobby}>Crear Sala</button>
                <hr />
                <input
                    type="text"
                    value={joinCode}
                    onChange={(e) => setJoinCode(e.target.value)}
                    placeholder="Código de la sala"
                />
                <button onClick={joinLobby}>Unirse a Sala</button>
            </div>
        </div>
    );
};

export default Lobby;
