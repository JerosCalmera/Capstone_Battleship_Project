import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { ReactComponentElement, useEffect, useState } from "react";
import Grids from "../components/Grids";

interface Chat {
    id: number;
    text: string;
}

function GameBoard() {
    const BASE_URL = "http://192.168.4.133"

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
    const [chat, setChat] = useState<string[]>(["", "", "", "", "", "", "", "", "", ""])
    const [chatEntry, setChatEntry] = useState<string>("")
    const [player1Data, setPlayer1Data] = useState<string>("Player 1")
    const [player2Data, setPlayer2Data] = useState<string>("Player 2")


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

            client.subscribe("/topic/placement", (message: any) => {
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

            client.subscribe("/topic/playerData1", (message: any) => {
                setPlayer1Data(message.body.slice(12, -2))
            });


            client.subscribe("/topic/playerData2", (message: any) => {
                setPlayer2Data(message.body.slice(12, -2))
            });

            client.send("/app/hello", {}, JSON.stringify(`Client Connected on ${port}`));
            setServerStatus(true);

            client.subscribe("/topic/gameUpdate", (message: any) => {
                setPlayer1Data(message.body.slice(12, -2))
            });

            client.subscribe("/topic/startup", (message: any) => {
                setPlayer1Data(message.body.slice(12, -2))
            });

            client.ws.onclose = () => {
                (console.log("Connection terminated"))
                setServerStatus(false)
            };
            setStompClient(client)


        });
    }, [attemptReconnect])

    useEffect(() => {
        if (player1Data.includes(savedName)) {
            setPlayer1Data(player1Data);
            setPlayer2Data(player2Data)
        }
        else if (player2Data.includes(savedName)) {
            setPlayer1Data(player2Data)
            setPlayer2Data(player1Data)
        }
        // stompClient.send("/app/startup", {}, JSON.stringify(savedName));
        // stompClient.send("/app/gameUpdate", {}, JSON.stringify(savedName));
    }, [player2Data])


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
    const restart = () => {
        stompClient.send("/app/restart", {}, JSON.stringify(passwordEntry));
        serverSetMessageLog("")
        setShipInfo([])
        setEnemyShipInfo([])
        setEnemyShipDamage([])
        setShipDamage([])
        setPassword("")
        setPasswordEntry("")
        setPlayerName("")
        setSaveName("name")
        setPlayer1Data("")
        setPlayer2Data("")
    }
    const resetPlacement = () => {
        stompClient.send("/app/placement2", {}, JSON.stringify("Clear"));
    }


    return (
        <>
            {serverStatus == true ? <h5>Connected to game server</h5> : <div><h5>Not connected to game server</h5> <button className="button" onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button></div>}
            <h5>{serverMessageLog}</h5><button className="button" onClick={restart}>Restart</button>
            {serverMessageLog === "Server: Room saved!" && passwordEntry.length < 1 ? serverSetMessageLog("Server: Another player has started a room") : null}
            <button className="button" onClick={resetPlacement}>Reset Placement</button>
            {/* <Grids hidden={hidden} savedName={savedName} shipInfo={shipInfo} shipDamage={shipDamage} enemyShipInfo={enemyShipInfo} enemyShipDamage={enemyShipDamage} stompClient={stompClient} /> */}
            {serverMessageLog === "Server: Room saved!" ?
                <div className="startupOuter">
                    <h3 >Room number: {passwordEntry}</h3 >
                    <h3>Waiting on other player.....</h3></div >
                : serverMessageLog === "Server: Rooms synced" ?
                    <div>
                        <Grids player1Data={player1Data} player2Data={player2Data} savedName={savedName} shipInfo={shipInfo} shipDamage={shipDamage} enemyShipInfo={enemyShipInfo} enemyShipDamage={enemyShipDamage} stompClient={stompClient} />
                        <button className="button" onClick={resetPlacement}>Reset Placement</button>
                    </div> : null}
            {savedName != "name" && serverMessageLog != "Server: Room saved!" ? serverMessageLog != "Server: Rooms synced" ?
                <div className="startupOuter">
                    <h3>Please enter the room number....</h3>
                    <input className="input" name="room" value={password} onChange={(e) => setPassword(e.target.value)}></input>
                    <button className="button" onClick={auth}>Send</button>
                </div> : null : null}
            {savedName === "name" ?
                <div className="startupOuter">
                    <h3> Welcome to Solar Fury, Please enter your name....</h3>
                    <input className="input" name="name" value={playerName} onChange={(e) => setPlayerName(e.target.value)}></input>
                    <button className="button" onClick={saveName}>Save</button>
                </div> : null}
            {savedName != "name" ?
                <div className="chatBox">
                    Chat: <br />
                    {chat.map((message, index) => (
                        <li className="chatList" key={index}>{message}<br /></li>
                    ))}
                    <br />
                    <input className="input" name="chat" onChange={(e) => setChatEntry(e.target.value)}></input>
                    <button className="button" onClick={chatSend}>Send</button>
                </div> : "Enter a name to chat"}
        </>
    )
}

export default GameBoard
