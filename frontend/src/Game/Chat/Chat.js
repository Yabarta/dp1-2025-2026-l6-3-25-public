import React, { useState, useEffect } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './chatStyles.css'

export default function Chat(props){
    const [messages, setMessages] = useState([])
    const [message, setMessage] = useState('')
    const [nickname, setNickname] = useState('')
    const [stompClient, setStompClient] = useState([])

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');
        const client = Stomp.over(socket);

        client.connect({}, () => {
            client.subscribe('/topic/messages', (message) => {
                const receivedMessage = JSON.parse(message.body)
                setMessages((prevMessages) => [...prevMessages, receivedMessage])
            })
        })
        setStompClient(client)
        return () =>{
            client.disconnect()
        }
    }, [])

    const handleMessageChange = (msg) => {
        setMessage(msg.target.value)
        console.log("Nickname: "+props.nickname)
        setNickname(props.nickname)
    }

    const sendMessage = () => {
        if(message.trim()){
            const chatMessage = {
                nickname: nickname,
                message: message.trim()
            }
            stompClient.send('/app/chat', {}, JSON.stringify(chatMessage))
            setMessage('')
        }
    }


    return(
        <div className='chat'>
            {/* ZONA DE MENSAJES (Scrollable) */}
            <ul className='lista'>
                {messages.map((msg, i) => (
                    <li key={i} className='elemento-lista'>
                        <div style={{ overflow: 'hidden' }}> {/* Evita que textos largos rompan el layout */}
                            <div style={{ fontWeight: 600, fontSize: '14px' }}><strong>{msg.nickname || 'Anonimo'}</strong></div>
                            <div style={{ color: '#333', fontSize: '14px', wordWrap: 'break-word', paddingLeft: "1%" }}>{msg.message}</div>
                        </div>
                    </li>
                ))}
            </ul>

            {/* ZONA DE INPUTS (Fija abajo) */}
            <div style={{ display: 'flex', gap: 8, padding: 10}}>
                <input
                    placeholder="mensaje..."
                    value={message}
                    onChange={handleMessageChange}
                    onKeyDown={(e) => { if (e.key === 'Enter') sendMessage(); }}
                    style={{ 
                        flex: 1,             // Ocupa el resto del espacio
                        padding: 8, 
                        borderRadius: 4,
                        border: '1px solid'
                    }}
                />
            </div>
        </div>
    )
}