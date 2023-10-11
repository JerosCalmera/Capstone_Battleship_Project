import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { ReactComponentElement, useEffect, useState } from "react";
import Grids from "../components/Grids";

function GameBoard() {
    const BASE_URL = "http://192.168.248.133."

    const [stompClient, setStompClient] = useState<Stomp.Client>(Stomp.over(new SockJS(`${BASE_URL}:8081/game`)));
    const [severStatus, setServerStatus] = useState(false)
    const [attemptReconnect, setAttemptReconnect] = useState(0)
    const [serverMessageLog, serverSetMessageLog] = useState("")
    const [shipInfo, setShipInfo] = useState<string[]>([])
    const [enemyShipInfo, setEnemyShipInfo] = useState<string[]>([])
    const [enemyShipDamage, setEnemyShipDamage] = useState<string[]>([])
    const [shipDamage, setShipDamage] = useState<string[]>([])
    const [password, setPassword] = useState<string>("Pears")
    const [passwordEntry, setPasswordEntry] = useState<string>("Apples")
    const [playerName, setPlayerName] = useState<string>("")
    const [savedName, setSaveName] = useState<string>("")

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

        stompClient.send("/app/room", {}, JSON.stringify(password));
        setPasswordEntry(password)
    }

    const nameSave = () => {
        setSaveName(playerName)
    }

    return (
        <>
            {severStatus == true ? <h4>Connected to game server</h4> : <div><h4>Not connected to game server</h4> <button onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button></div>}
            <h4>{serverMessageLog}</h4>
            {savedName === "" ?
                <div>
                    <h1>Please enter your name....</h1>
                    <input onChange={(e) => setPlayerName(e.target.value)}></input>
                    <button onClick={nameSave}>Save</button>
                </div>
                :
                serverMessageLog === "Room saved!" && playerName === savedName ? <div>
                    < h1 > Room number: {passwordEntry}</h1 >
                    <h1>Waiting on other player.....</h1></div > :
                    serverMessageLog === "Rooms synced" ?
                        <div>
                            <Grids shipInfo={shipInfo} shipDamage={shipDamage} enemyShipInfo={enemyShipInfo} enemyShipDamage={enemyShipDamage} stompClient={stompClient} />
                        </div>
                        :
                        <div>
                            <h1>Please enter the room number....</h1>
                            <input onChange={(e) => setPassword(e.target.value)}></input>
                            <button onClick={auth}>Save</button>
                        </div>
            }
        </>
    )
}

export default GameBoard
