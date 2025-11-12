import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const LobbyScreen = () => {
    const { id } = useParams();
    const [lobby, setLobby] = useState(null);
    const [players, setPlayers] = useState([]);
    const [stompClient, setStompClient] = useState(null);

    useEffect(() => {
        const fetchLobby = async () => {
            try {
                const response = await axios.get(`/api/salas/${id}`);
                setLobby(response.data);
                setPlayers(response.data.jugadores);
            } catch (error) {
                console.error("Error fetching lobby", error);
            }
        };

        fetchLobby();
    }, [id]);

    useEffect(() => {
        
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
        

        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
    }, [lobby, stompClient]);

    if (!lobby) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h1>Lobby</h1>
            <div>
                <h2>Sala: {lobby.codigoDeUnion}</h2>
                <h3>Jugadores:</h3>
                <ul>
                    {players.map((player, index) => (
                        <li key={index}>{player}</li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default LobbyScreen;
