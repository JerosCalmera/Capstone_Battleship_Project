import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { useEffect, useState } from "react";
import Grids from "../components/Grids";
import StartUp from "../components/StartUp";


function GameBoard() {
    const BASE_URL = "http://localhost"

    const [stompClient, setStompClient] = useState<Stomp.Client>(Stomp.over(new SockJS(`${BASE_URL}:8081/game`)));
    const [serverStatus, setServerStatus] = useState(false)
    const [attemptReconnect, setAttemptReconnect] = useState(0)
    const [serverMessageLog, serverSetMessageLog] = useState("")
    const [hidden, setHidden] = useState("")

    const [leaderBoard, setLeaderBoard] = useState<string[]>([""])
    const [leaderBoardCheck, setLeaderBoardCheck] = useState<boolean>(false)

    const [shipInfo, setShipInfo] = useState<string>("")
    const [shipDamage, setShipDamage] = useState<string>("")

    const [enemyShipDamage, setEnemyShipDamage] = useState<string>("")

    const [damageCheck, setDamageCheck] = useState<string>("")

    const [missCheck, setMissCheck] = useState<string>("")
    const [miss, setMiss] = useState<string>("")
    const [enemyMiss, setEnemyMiss] = useState<string>("")

    const [password, setPassword] = useState<string>("")
    const [passwordEntry, setPasswordEntry] = useState<string>("")

    const [playerName, setPlayerName] = useState<string>("")
    const [playerNumber, setPlayerNumber] = useState<string>("")
    const [savedName, setSaveName] = useState<string>("name")
    const [ready, setReady] = useState<string>("name")

    const [chat, setChat] = useState<string[]>(["", "", "", "", "", "", "", "", "", ""])
    const [chatEntry, setChatEntry] = useState<string>("")

    const [player1Data, setPlayer1Data] = useState<string>("Player 1")
    const [player2Data, setPlayer2Data] = useState<string>("Player 2")
    const [player2Name, setPlayer2Name] = useState<string>("Player 2")

    const [placedShip, setPlacedShip] = useState<string>("")
    const [cellStorage, setCellStorage] = useState<string>("")

    const [gameInfo, setGameInfo] = useState<string>("")
    const [turn, setTurn] = useState<string>("Waiting")
    const [turnNumber, setTurnNumber] = useState<number>(-1)


    useEffect(() => {
        const port = 8081;
        const socket = new SockJS(`${BASE_URL}:${port}/game`);
        const client = Stomp.over(socket);

        client.connect({}, () => {
            console.log("Connected to server");
            client.subscribe("/topic/connect", (message: any) => {
                serverSetMessageLog(message.body.slice(12, -2))
            });
            client.subscribe("/topic/hidden", (message: any) => {
                setHidden(message.body.slice(12, -2))
            });
            client.subscribe("/topic/gameInfo", (message: any) => {
                setGameInfo(message.body.slice(12, -2))
            });
            client.subscribe("/topic/gameData", (message: any) => {
                setCellStorage(message.body.slice(12, -2))
            });
            client.subscribe("/topic/turn", (message: any) => {
                setTurn(message.body.slice(12, -2))
            });
            client.subscribe("/topic/gameData2", (message: any) => {
                setShipDamage(message.body.slice(12, -2))
            });
            client.subscribe("/topic/placement", (message: any) => {
                setShipDamage(message.body.slice(12, -2))
            });
            client.subscribe("/topic/miss", (message: any) => {
                setMissCheck(message.body.slice(12, -2))
            });
            client.subscribe("/topic/chat", (message: any) => {
                const newMessage: string = message.body.slice(12, -2)
                setChat((prevChat) => {
                    const updatedChat = [...prevChat, newMessage];
                    return updatedChat.slice(-10)
                })
            });
            client.subscribe("/topic/playerData1", (message: any) => {
                setPlayer1Data(message.body.slice(12, -2))
            })
            client.subscribe("/topic/playerData2", (message: any) => {
                setPlayer2Data(message.body.slice(12, -2))
            });

            client.subscribe("/topic/leaderBoard", (message: any) => {
                const leaderBoardEntry: string = message.body.slice(12, -2)
                setLeaderBoard((prevLeader) => {
                    const updatedLeader = [...prevLeader, leaderBoardEntry];
                    return updatedLeader
                })
            });

            client.send("/app/hello", {}, JSON.stringify(`Client Connected on ${port}`));
            setServerStatus(true);

            client.subscribe("/topic/gameUpdate", (message: any) => {
                setPlayer1Data(message.body.slice(12, -2))
            });

            client.subscribe("/topic/enemyDamage", (message: any) => {
                setDamageCheck(message.body.slice(12, -2))
            });

            client.subscribe("/topic/startup", (message: any) => {
                setPlayer1Data(message.body.slice(12, -2))
            });

            client.subscribe("/topic/placement2", (message: any) => {
                setPlacedShip(message.body.slice(12, -2))
            });

            client.ws.onclose = () => {
                (console.log("Connection terminated"))
                setServerStatus(false)
            };
            setStompClient(client)
        });
    }, [attemptReconnect])

    useEffect(() => {
        if (player1Data.includes(savedName) && !player1Data.includes("Lvl") && !player2Data.includes("Lvl")) {
            setPlayerNumber(player1Data.slice(0,5))
            setPlayer1Data(player1Data.slice(5))
            setPlayer2Data(player2Data.slice(5))
            setPlayer2Name(player2Data.slice(5))
        }
        else if (player2Data.includes(savedName)) {
            setPlayerNumber(player2Data.slice(0,5))
            setPlayer1Data(player2Data.slice(5))
            setPlayer2Data(player1Data.slice(5))
            setPlayer2Name(player1Data.slice(5))
        }
    }, [player2Data])


    useEffect(() => {
        setTurnNumber(turnNumber + 1)
    }, [turn])

    useEffect(() => {
        if (missCheck.includes(savedName)) {
            setMiss(miss + missCheck);
            console.log(miss)
        }
        else if (!missCheck.includes(savedName)) {
            setEnemyMiss(enemyMiss + missCheck)
            console.log(enemyMiss)
        }
    }, [missCheck])

    useEffect(() => {
        if (serverMessageLog === "Game server ready....") {
            if (leaderBoardCheck == false) {
                stompClient.send("/app/leaderBoard", {}, JSON.stringify("Game start"));
                setLeaderBoardCheck(true)
            }
        }
    }, [serverMessageLog])

    useEffect(() => {
        const toTrim = cellStorage;
        if (toTrim.includes(savedName)) {
            const trimmed: string = toTrim.replace(savedName, '');
            setShipInfo(trimmed);
        }
    }, [cellStorage]);


    useEffect(() => {
        if (!damageCheck.includes(savedName)) {
            setEnemyShipDamage(enemyShipDamage + damageCheck.slice(0, 2))
        }
        else {
            setShipDamage(shipDamage + damageCheck.slice(0, 2))
        }
        console.log(shipInfo)
    }, [damageCheck]);

    useEffect(() => {
        if (hidden.includes(savedName)) {
            stompClient.send("/app/chat", {}, JSON.stringify("Admin: Sorry " + savedName + " is too similar to an existing username!"));
            setSaveName("name");
            setHidden("");
        }
    }, [hidden, chat, serverMessageLog]);

    const auth = () => {
        if (password.length < 4) {
            stompClient.send("/app/chat", {}, JSON.stringify("Admin: Sorry room numbers must be minimum of 4 characters long!"));
        }
        else {
            setPasswordEntry(password)
            stompClient.send("/app/room", {}, JSON.stringify(password));
        }
    }

    const generate = () => {
        const randomNumber = Math.floor(Math.random() * 10000)
        const roomNumber = randomNumber.toString().padStart(4, "0");
        setPasswordEntry(roomNumber)
        stompClient.send("/app/room", {}, JSON.stringify(roomNumber));
    }

    const saveName = () => {
        if (playerName.length < 5) {
            stompClient.send("/app/chat", {}, JSON.stringify("Admin: Sorry usernames must be minimum of 5 characters long!"));
        }
        else if (playerNumber.length > 0 && playerNumber.length < 5 || playerNumber.length > 5) {
            stompClient.send("/app/chat", {}, JSON.stringify("Admin: Sorry player numbers must be exactly 5 numbers long!"));
        }
        else {
            setSaveName(playerName);
            stompClient.send("/app/name", {}, JSON.stringify(playerNumber + playerName));
            console.log(playerNumber)
            console.log(playerName)
            setReady("ready");
        }

    }
    const chatSend = () => {
        stompClient.send("/app/chat", {}, JSON.stringify(savedName + ": " + chatEntry));
        setChatEntry("")
    }
    const restart = () => {
        stompClient.send("/app/restart", {}, JSON.stringify(passwordEntry));
        location.reload()
    }
    const resetPlacement = () => {
        console.log(cellStorage)
        console.log(shipInfo)
        console.log(savedName)
        console.log(playerName)
        stompClient.send("/app/placement2", {}, JSON.stringify("Clear"));
    }


    return (
        <>
            {serverStatus == true ? <h5>Connected to game server</h5> :
                <div>
                    <h5>Not connected to game server</h5>
                    <button className="button" onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button>

                </div>}
            <h5>{serverMessageLog}</h5>
            <button className="button" onClick={restart}>Restart</button>
            <button className="button" onClick={resetPlacement}>Reset</button>
            {serverMessageLog === "Server: Room saved!" && passwordEntry.length < 1 ? serverSetMessageLog("Server: Another player has started a room") : null}
            {serverMessageLog === "Server: Rooms synced" ?
                <div className="gameInfoOuter">
                    <div className="gameInfo">
                        <h3>Turn: {turnNumber} {turn}</h3>
                        <h3>{gameInfo}</h3>
                    </div>
                </div> : null}
            {serverMessageLog === "Server: Room saved!" && savedName != "name" && passwordEntry.length > 0 ?
                <div className="startupOuter">
                    <h3 >Room number: {passwordEntry}</h3 >
                    <h3>Waiting on other player.....</h3></div >
                : serverMessageLog === "Server: Rooms synced" ?
                    <div>
                        <Grids playerNumber={playerNumber} playerName={playerName} turn={turn} miss={miss} enemyMiss={enemyMiss} player2Name={player2Name} chat={chat}
                            placedShip={placedShip} player1Data={player1Data} setPlacedShip={setPlacedShip}
                            player2Data={player2Data} savedName={savedName} shipInfo={shipInfo}
                            shipDamage={shipDamage} enemyShipDamage={enemyShipDamage}
                            stompClient={stompClient} />

                    </div> : null}
            <StartUp playerNumber={playerNumber} setPlayerNumber={setPlayerNumber} hidden={hidden} ready={ready} savedName={savedName} serverMessageLog={serverMessageLog} password={password}
                setPassword={setPassword} auth={auth} generate={generate} playerName={playerName} chat={chat}
                saveName={saveName} chatSend={chatSend} setPlayerName={setPlayerName} setChatEntry={setChatEntry}
                leaderBoard={leaderBoard} />
        </>
    )
}

export default GameBoard
