import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { ReactComponentElement, useEffect, useState } from "react";
import Grids from "../components/Grids";

interface Chat {
    id: number;
    text: string;
}

function GameBoard() {
    const BASE_URL = "http://10.149.50.208"

    const [stompClient, setStompClient] = useState<Stomp.Client>(Stomp.over(new SockJS(`${BASE_URL}:8081/game`)));
    const [serverStatus, setServerStatus] = useState(false)
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
    const [chat, setChat] = useState<string[]>(["", "", "", "", "", "", "", "", "", ""])
    const [chatEntry, setChatEntry] = useState<string>("")
    const [hidden, setHidden] = useState<string>("")


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

            client.subscribe("/topic/name", (message: any) => {
                setShipDamage(message.body.slice(12, -2))
            });

            client.subscribe("/topic/chat", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                setChat((prevChat) => {
                    const updatedChat = [...prevChat, newMessage];
                    return updatedChat.slice(-10)
                });
            });

            client.subscribe("/topic/hidden", (message: any) => {
                setHidden(message.body.slice(12, -2))
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

    const saveName = () => {
        setSaveName(playerName)
        stompClient.send("/app/name", {}, JSON.stringify(playerName));
    }

    const chatSend = () => {
        stompClient.send("/app/chat", {}, JSON.stringify(savedName + ": " + chatEntry));
        setChatEntry("")
    }

    return (
        <>
            {serverStatus == true ? <h4>Connected to game server</h4> : <div><h4>Not connected to game server</h4> <button onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button></div>}
            <h4>{serverMessageLog}</h4>
            {serverMessageLog === "Server: Player already exists!" ? savedName != "name" ? setSaveName("name") : serverSetMessageLog("Server: Sorry that player already exists!") : null}
            {serverMessageLog === "Server: Room saved!" && passwordEntry.length < 1 ? serverSetMessageLog("Server: Another player has started a room") : null}
            {serverMessageLog === "Server: Room saved!" ?
                <div>
                    < h1 >Room number: {passwordEntry}</h1 >
                    <h1>Waiting on other player.....</h1></div >
                : serverMessageLog === "Server: Rooms synced" ?
                    <div>
                        <Grids savedName={savedName} shipInfo={shipInfo} shipDamage={shipDamage} enemyShipInfo={enemyShipInfo} enemyShipDamage={enemyShipDamage} stompClient={stompClient} />
                    </div> : null}
            {savedName != "name" && serverMessageLog != "Server: Room saved!" ? serverMessageLog != "Server: Rooms synced" ?
                <div>
                    <h1>Please enter the room number....</h1>
                    <input name="room" value={password} onChange={(e) => setPassword(e.target.value)}></input>
                    <button onClick={auth}>Send</button>
                </div> : null : null}
            {savedName === "name" ?
                <div>
                    <h1>Please enter your name....</h1>
                    <input name="name" value={playerName} onChange={(e) => setPlayerName(e.target.value)}></input>
                    <button onClick={saveName}>Save</button>
                </div> : null}
            {savedName != "name" ?
                <div className="chatBox">
                    Chat: <br />
                    {chat.map((message, index) => (
                        <ol key={index}>{message}<br /></ol>
                    ))}
                    <br />
                    <input name="chat" onChange={(e) => setChatEntry(e.target.value)}></input>
                    <button onClick={chatSend}>Send</button>
                </div> : "Enter a name to chat"}
        </>
    )
}

export default GameBoard
