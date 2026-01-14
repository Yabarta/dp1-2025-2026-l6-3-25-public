import React, { useState, useEffect } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './chatStyles.css'

export default function Chat({nickname, id, isPlayer}) {
    const [messages, setMessages] = useState([])
    const [message, setMessage] = useState('')
    const [stompClient, setStompClient] = useState([])
    
    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');
        const client = Stomp.over(socket);

        client.connect({}, () => {
            client.subscribe(`/topic/messages/${id}`, (message) => {
                const receivedMessage = JSON.parse(message.body)
                setMessages((prevMessages) => [...prevMessages, receivedMessage])
            })
        })
        setStompClient(client)
        return () =>{
            client.disconnect()
        }
    }, [id])

    const handleMessageChange = (msg) => {
        setMessage(msg.target.value)
    }

    const sendMessage = () => {
        if(message.trim()){
            const chatMessage = {
                nickname: nickname,
                message: message.trim()
            }
            stompClient.send(`/app/chat/${id}`, {}, JSON.stringify(chatMessage))
            setMessage('')
        }
    }


    return(
        <>
            <div className='chat'>
            {/* ZONA DE MENSAJES */}
            <ul className='lista'>
                {messages.map((msg, i) => (
                    <li key={i} className='elemento-lista'>
                        <div style={{ overflow: 'hidden' }}> {/* Evita que textos largos rompan el layout */}
                            <div style={{ color: '#d1cfcf',fontWeight: 600, fontSize: '20px' }}><strong>{msg.nickname || 'Anonimo'}</strong></div>
                            <div style={{ fontSize: '17px', wordWrap: 'break-word', paddingLeft: "1%" }}>{msg.message}</div>
                        </div>
                    </li>
                ))}
            </ul>

            {/* ZONA DE INPUTS (Fija abajo) */}
            </div>
            <div style={{ 
                padding: '10px', 
                background: 'rgba(0,0,0,0.2)',
                flexShrink: 0
                }}>
            <input
                className='inputMessage'
                placeholder="mensaje..."
                value={message}
                onChange={handleMessageChange}
                onKeyDown={(e) => { if (e.key === 'Enter') sendMessage(); }}
                disabled = {!isPlayer}
            />
                </div>
        </>
    )
}