import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { ReactComponentElement, useEffect, useState } from "react";
import Grids from "../components/Grids";

function GameBoard() {
    const BASE_URL = "http://192.168.1.231"

    const [stompClient, setStompClient] = useState<Stomp.Client>(Stomp.over(new SockJS(`${BASE_URL}:8081/game`)));
    const [severStatus, setServerStatus] = useState(false)
    const [attemptReconnect, setAttemptReconnect] = useState(0)
    const [serverMessageLog, serverSetMessageLog] = useState("")
    const [shipInfo, setShipInfo] = useState<string[]>([])
    const [enemyShipInfo, setEnemyShipInfo] = useState<string[]>([])
    const [enemyShipDamage, setEnemyShipDamage] = useState<string[]>([])
    const [shipDamage, setShipDamage] = useState<string[]>([])
    const [password, setPassword] = useState<string>("")
    const [passwordEntry, setPasswordEntry] = useState<string>("")
    const [playerName, setPlayerName] = useState<string>("")
    const [savedName, setSaveName] = useState<string>("name")
    const [roomReady, setRoomReady] = useState<boolean>(false)
    const [chat, setChat] = useState<string>("")

    useEffect(() => {
        const port = 8081;
        const socket = new SockJS(`${BASE_URL}:${port}/game`);
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("Connected to server");
            client.subscribe("/topic/connect", (message: any) => {
                serverSetMessageLog(message.body.slice(12, -2))
            });

            client.subscribe("/topic/gameData", (message: any) => {
                setShipInfo(message.body.slice(12, -2))

            });

            client.subscribe("/topic/gameData2", (message: any) => {
                setShipDamage(message.body.slice(12, -2))

            });

            client.subscribe("/topic/chat", (message: any) => {
                setChat(message.body.slice(12, -2))
            });

            client.send("/app/hello", {}, JSON.stringify(`Client Connected on ${port}`));
            setServerStatus(true);

            client.ws.onclose = () => {
                (console.log("Connection terminated"))
                setServerStatus(false)
            };
            setStompClient(client)
        });
    }, [attemptReconnect])


    const auth = () => {
        setPasswordEntry(password)
        stompClient.send("/app/room", {}, JSON.stringify(password));
    }

    const nameSave = () => {
        setSaveName(playerName)
    }

    const loginProcess = () => {
        if (serverMessageLog === "Rooms synced")
            return (
                <div>
                    <Grids shipInfo={shipInfo} shipDamage={shipDamage} enemyShipInfo={enemyShipInfo} enemyShipDamage={enemyShipDamage} stompClient={stompClient} />
                </div>);
        else if (serverMessageLog === "Room saved!" && savedName != "name")
            return (
                <div>
                    < h1 > Room number: {passwordEntry}</h1 >
                    <h1>Waiting on other player.....</h1></div >
            );
        else if (savedName != "name" && serverMessageLog != "Rooms synced") {
            return (
                <div>
                    <h1>Please enter the room number....</h1>
                    <input name="room" value={password} onChange={(e) => setPassword(e.target.value)}></input>
                    <button onClick={auth}>Send</button>
                </div>);
        }
        else if (savedName === "name")
            return (
                <div>
                    <h1>Please enter your name....</h1>
                    <input name="name" onChange={(e) => setPlayerName(e.target.value)}></input>
                    <button onClick={nameSave}>Save</button>
                </div>)
    }

    return (
        <>
            {severStatus == true ? <h4>Connected to game server</h4> : <div><h4>Not connected to game server</h4> <button onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button></div>}
            <h4>{serverMessageLog}</h4>
            Chat: <h4>{chat}</h4>
            {loginProcess()}
        </>
    )
}

export default GameBoard
