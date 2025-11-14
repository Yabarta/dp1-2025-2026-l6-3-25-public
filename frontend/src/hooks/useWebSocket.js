import { useEffect, useMemo, useState } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import tokenService from '../services/token.service';

const SOCKET_URL = process.env.REACT_APP_WS_URL ?? 'http://localhost:8080/ws';

const useWebSocket = (endpoint, topic, payload) => {
    const [data, setData] = useState(null);
    const jwt = tokenService.getLocalAccessToken();
    const serializedPayload = useMemo(() => {
        if (payload === undefined) {
            return undefined;
        }
        try {
            return JSON.stringify(payload);
        } catch (error) {
            console.error('Unable to serialise WebSocket payload', error);
            return undefined;
        }
    }, [payload]);

    useEffect(() => {
        if (!endpoint || !topic) {
            return undefined;
        }

        const socket = new SockJS(SOCKET_URL);
        const stompClient = Stomp.over(() => socket);
        stompClient.reconnectDelay = 5000;
        stompClient.debug = () => {};

        const headers = jwt ? { Authorization: `Bearer ${jwt}` } : {};

        stompClient.connect(headers, () => {
            stompClient.subscribe(topic, (response) => {
                try {
                    setData(JSON.parse(response.body));
                } catch (error) {
                    console.error('Error parsing WebSocket payload', error);
                }
            });

            if (serializedPayload !== undefined) {
                stompClient.send(endpoint, {}, serializedPayload);
            } else {
                stompClient.send(endpoint, {});
            }
        }, (error) => {
            console.error('WebSocket connection error:', error);
        });

        return () => {
            if (stompClient.connected) {
                stompClient.disconnect();
            } else {
                stompClient.deactivate();
            }
        };
    }, [endpoint, topic, jwt, serializedPayload]);

    return data;
};

export default useWebSocket;