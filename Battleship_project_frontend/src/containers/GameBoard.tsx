import Stomp from "stompjs";
import SockJS from "sockjs-client";
import { useEffect, useState } from "react";

function GameBoard() {

    const [stompClient, setStompClient] = useState<Stomp.Client>(Stomp.over(new SockJS(`http://localhost:8081/game`)));
    const [severStatus, setServerStatus] = useState(false)
    const [attemptReconnect, setAttemptReconnect] = useState(0)
    const [serverMessageLog, serverSetMessageLog] = useState("")
    const [shipInfo, setShipInfo] = useState("")
    const [enemyShipInfo, setEnemyShipInfo] = useState("")
    const [enemyShipDamage, setEnemyShipDamage] = useState("")
    const [shipDamage, setShipDamage] = useState("")
    const [loaded, setLoaded] = useState(false)


    useEffect(() => {
        const port = 8081;
        const socket = new SockJS(`http://localhost:${port}/game`);
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
    }, [attemptReconnect]);


    const clickedCell = (value: String) => {
        console.log(value)
        stompClient.send("/app/gameData", {}, JSON.stringify(value));
    }

    const clickedEnemyCell = (value: String) => {
        console.log(value)
    }


    return (
        <>
            {severStatus == true ? <h4>Connected to game server</h4> : <div><h4>Not connected to game server</h4> <button onClick={() => setAttemptReconnect(attemptReconnect + 1)}>Reconnect</button></div>}
            <h4>{serverMessageLog}</h4>
            <div className="gameBoardOuter">
                <div className="gameBoardRender">
                    <h2>Player 1</h2>
                    <ul>
                        <button name="1" className="endCell">A</button>
                        <button name="A1" onClick={() => clickedCell("A1")} className={shipInfo.includes("A1") ? "cellShip" : shipDamage.includes("A1") ? "cellDamaged" : "cell"}>A1</button>
                        <button name="A2" onClick={() => clickedCell("A2")} className={shipInfo.includes("A2") ? "cellShip" : shipDamage.includes("A2") ? "cellDamaged" : "cell"}>A2</button>
                        <button name="A3" onClick={() => clickedCell("A3")} className={shipInfo.includes("A3") ? "cellShip" : shipDamage.includes("A3") ? "cellDamaged" : "cell"}>A3</button>
                        <button name="A4" onClick={() => clickedCell("A4")} className={shipInfo.includes("A4") ? "cellShip" : shipDamage.includes("A4") ? "cellDamaged" : "cell"}>A4</button>
                        <button name="A5" onClick={() => clickedCell("A5")} className={shipInfo.includes("A5") ? "cellShip" : shipDamage.includes("A5") ? "cellDamaged" : "cell"}>A5</button>
                        <button name="A6" onClick={() => clickedCell("A6")} className={shipInfo.includes("A6") ? "cellShip" : shipDamage.includes("A6") ? "cellDamaged" : "cell"}>A6</button>
                        <button name="A7" onClick={() => clickedCell("A7")} className={shipInfo.includes("A7") ? "cellShip" : shipDamage.includes("A7") ? "cellDamaged" : "cell"}>A7</button>
                        <button name="A8" onClick={() => clickedCell("A8")} className={shipInfo.includes("A8") ? "cellShip" : shipDamage.includes("A8") ? "cellDamaged" : "cell"}>A8</button>
                        <button name="A9" onClick={() => clickedCell("A9")} className={shipInfo.includes("A9") ? "cellShip" : shipDamage.includes("A9") ? "cellDamaged" : "cell"}>A9</button>
                        <button name="A10" onClick={() => clickedCell("A10")} className={shipInfo.includes("A10") ? "cellShip" : shipDamage.includes("A10") ? "cellDamaged" : "cell"}>A10</button>

                        <button name="2" className="endCell">B</button>
                        <button name="B1" onClick={() => clickedCell("B1")} className={shipInfo.includes("B1") ? "cellShip" : shipDamage.includes("B1") ? "cellDamaged" : "cell"}>B1</button>
                        <button name="B2" onClick={() => clickedCell("B2")} className={shipInfo.includes("B2") ? "cellShip" : shipDamage.includes("B2") ? "cellDamaged" : "cell"}>B2</button>
                        <button name="B3" onClick={() => clickedCell("B3")} className={shipInfo.includes("B3") ? "cellShip" : shipDamage.includes("B3") ? "cellDamaged" : "cell"}>B3</button>
                        <button name="B4" onClick={() => clickedCell("B4")} className={shipInfo.includes("B4") ? "cellShip" : shipDamage.includes("B4") ? "cellDamaged" : "cell"}>B4</button>
                        <button name="B5" onClick={() => clickedCell("B5")} className={shipInfo.includes("B5") ? "cellShip" : shipDamage.includes("B5") ? "cellDamaged" : "cell"}>B5</button>
                        <button name="B6" onClick={() => clickedCell("B6")} className={shipInfo.includes("B6") ? "cellShip" : shipDamage.includes("B6") ? "cellDamaged" : "cell"}>B6</button>
                        <button name="B7" onClick={() => clickedCell("B7")} className={shipInfo.includes("B7") ? "cellShip" : shipDamage.includes("B7") ? "cellDamaged" : "cell"}>B7</button>
                        <button name="B8" onClick={() => clickedCell("B8")} className={shipInfo.includes("B8") ? "cellShip" : shipDamage.includes("B8") ? "cellDamaged" : "cell"}>B8</button>
                        <button name="B9" onClick={() => clickedCell("B9")} className={shipInfo.includes("B9") ? "cellShip" : shipDamage.includes("B9") ? "cellDamaged" : "cell"}>B9</button>
                        <button name="B10" onClick={() => clickedCell("B10")} className={shipInfo.includes("B10") ? "cellShip" : shipDamage.includes("B10") ? "cellDamaged" : "cell"}>B10</button>

                        <button name="3" className="endCell">C</button>
                        <button name="C1" onClick={() => clickedCell("C1")} className={shipInfo.includes("C1") ? "cellShip" : shipDamage.includes("C1") ? "cellDamaged" : "cell"}>C1</button>
                        <button name="C2" onClick={() => clickedCell("C2")} className={shipInfo.includes("C2") ? "cellShip" : shipDamage.includes("C2") ? "cellDamaged" : "cell"}>C2</button>
                        <button name="C3" onClick={() => clickedCell("C3")} className={shipInfo.includes("C3") ? "cellShip" : shipDamage.includes("C3") ? "cellDamaged" : "cell"}>C3</button>
                        <button name="C4" onClick={() => clickedCell("C4")} className={shipInfo.includes("C4") ? "cellShip" : shipDamage.includes("C4") ? "cellDamaged" : "cell"}>C4</button>
                        <button name="C5" onClick={() => clickedCell("C5")} className={shipInfo.includes("C5") ? "cellShip" : shipDamage.includes("C5") ? "cellDamaged" : "cell"}>C5</button>
                        <button name="C6" onClick={() => clickedCell("C6")} className={shipInfo.includes("C6") ? "cellShip" : shipDamage.includes("C6") ? "cellDamaged" : "cell"}>C6</button>
                        <button name="C7" onClick={() => clickedCell("C7")} className={shipInfo.includes("C7") ? "cellShip" : shipDamage.includes("C7") ? "cellDamaged" : "cell"}>C7</button>
                        <button name="C8" onClick={() => clickedCell("C8")} className={shipInfo.includes("C8") ? "cellShip" : shipDamage.includes("C8") ? "cellDamaged" : "cell"}>C8</button>
                        <button name="C9" onClick={() => clickedCell("C9")} className={shipInfo.includes("C9") ? "cellShip" : shipDamage.includes("C9") ? "cellDamaged" : "cell"}>C9</button>
                        <button name="C10" onClick={() => clickedCell("C10")} className={shipInfo.includes("C10") ? "cellShip" : shipDamage.includes("C10") ? "cellDamaged" : "cell"}>C10</button>


                        <button name="4" className="endCell">D</button>
                        <button name="D1" onClick={() => clickedCell("D1")} className={shipInfo.includes("D1") ? "cellShip" : shipDamage.includes("D1") ? "cellDamaged" : "cell"}>D1</button>
                        <button name="D2" onClick={() => clickedCell("D2")} className={shipInfo.includes("D2") ? "cellShip" : shipDamage.includes("D2") ? "cellDamaged" : "cell"}>D2</button>
                        <button name="D3" onClick={() => clickedCell("D3")} className={shipInfo.includes("D3") ? "cellShip" : shipDamage.includes("D3") ? "cellDamaged" : "cell"}>D3</button>
                        <button name="D4" onClick={() => clickedCell("D4")} className={shipInfo.includes("D4") ? "cellShip" : shipDamage.includes("D4") ? "cellDamaged" : "cell"}>D4</button>
                        <button name="D5" onClick={() => clickedCell("D5")} className={shipInfo.includes("D5") ? "cellShip" : shipDamage.includes("D5") ? "cellDamaged" : "cell"}>D5</button>
                        <button name="D6" onClick={() => clickedCell("D6")} className={shipInfo.includes("D6") ? "cellShip" : shipDamage.includes("D6") ? "cellDamaged" : "cell"}>D6</button>
                        <button name="D7" onClick={() => clickedCell("D7")} className={shipInfo.includes("D7") ? "cellShip" : shipDamage.includes("D7") ? "cellDamaged" : "cell"}>D7</button>
                        <button name="D8" onClick={() => clickedCell("D8")} className={shipInfo.includes("D8") ? "cellShip" : shipDamage.includes("D8") ? "cellDamaged" : "cell"}>D8</button>
                        <button name="D9" onClick={() => clickedCell("D9")} className={shipInfo.includes("D9") ? "cellShip" : shipDamage.includes("D9") ? "cellDamaged" : "cell"}>D9</button>
                        <button name="D10" onClick={() => clickedCell("D10")} className={shipInfo.includes("D10") ? "cellShip" : shipDamage.includes("D10") ? "cellDamaged" : "cell"}>D10</button>

                        <button name="5" className="endCell">E</button>
                        <button name="E1" onClick={() => clickedCell("E1")} className={shipInfo.includes("E1") ? "cellShip" : shipDamage.includes("E1") ? "cellDamaged" : "cell"}>E1</button>
                        <button name="E2" onClick={() => clickedCell("E2")} className={shipInfo.includes("E2") ? "cellShip" : shipDamage.includes("E2") ? "cellDamaged" : "cell"}>E2</button>
                        <button name="E3" onClick={() => clickedCell("E3")} className={shipInfo.includes("E3") ? "cellShip" : shipDamage.includes("E3") ? "cellDamaged" : "cell"}>E3</button>
                        <button name="E4" onClick={() => clickedCell("E4")} className={shipInfo.includes("E4") ? "cellShip" : shipDamage.includes("E4") ? "cellDamaged" : "cell"}>E4</button>
                        <button name="E5" onClick={() => clickedCell("E5")} className={shipInfo.includes("E5") ? "cellShip" : shipDamage.includes("E5") ? "cellDamaged" : "cell"}>E5</button>
                        <button name="E6" onClick={() => clickedCell("E6")} className={shipInfo.includes("E6") ? "cellShip" : shipDamage.includes("E6") ? "cellDamaged" : "cell"}>E6</button>
                        <button name="E7" onClick={() => clickedCell("E7")} className={shipInfo.includes("E7") ? "cellShip" : shipDamage.includes("E7") ? "cellDamaged" : "cell"}>E7</button>
                        <button name="E8" onClick={() => clickedCell("E8")} className={shipInfo.includes("E8") ? "cellShip" : shipDamage.includes("E8") ? "cellDamaged" : "cell"}>E8</button>
                        <button name="E9" onClick={() => clickedCell("E9")} className={shipInfo.includes("E9") ? "cellShip" : shipDamage.includes("E9") ? "cellDamaged" : "cell"}>E9</button>
                        <button name="E10" onClick={() => clickedCell("E10")} className={shipInfo.includes("E10") ? "cellShip" : shipDamage.includes("E10") ? "cellDamaged" : "cell"}>E10</button>

                        <button name="6" className="endCell">F</button>
                        <button name="F1" onClick={() => clickedCell("F1")} className={shipInfo.includes("F1") ? "cellShip" : shipDamage.includes("F1") ? "cellDamaged" : "cell"}>F1</button>
                        <button name="F2" onClick={() => clickedCell("F2")} className={shipInfo.includes("F2") ? "cellShip" : shipDamage.includes("F2") ? "cellDamaged" : "cell"}>F2</button>
                        <button name="F3" onClick={() => clickedCell("F3")} className={shipInfo.includes("F3") ? "cellShip" : shipDamage.includes("F3") ? "cellDamaged" : "cell"}>F3</button>
                        <button name="F4" onClick={() => clickedCell("F4")} className={shipInfo.includes("F4") ? "cellShip" : shipDamage.includes("F4") ? "cellDamaged" : "cell"}>F4</button>
                        <button name="F5" onClick={() => clickedCell("F5")} className={shipInfo.includes("F5") ? "cellShip" : shipDamage.includes("F5") ? "cellDamaged" : "cell"}>F5</button>
                        <button name="F6" onClick={() => clickedCell("F6")} className={shipInfo.includes("F6") ? "cellShip" : shipDamage.includes("F6") ? "cellDamaged" : "cell"}>F6</button>
                        <button name="F7" onClick={() => clickedCell("F7")} className={shipInfo.includes("F7") ? "cellShip" : shipDamage.includes("F7") ? "cellDamaged" : "cell"}>F7</button>
                        <button name="F8" onClick={() => clickedCell("F8")} className={shipInfo.includes("F8") ? "cellShip" : shipDamage.includes("F8") ? "cellDamaged" : "cell"}>F8</button>
                        <button name="F9" onClick={() => clickedCell("F9")} className={shipInfo.includes("F9") ? "cellShip" : shipDamage.includes("F9") ? "cellDamaged" : "cell"}>F9</button>
                        <button name="F10" onClick={() => clickedCell("F10")} className={shipInfo.includes("F10") ? "cellShip" : shipDamage.includes("F10") ? "cellDamaged" : "cell"}>F10</button>

                        <button name="7" className="endCell">G</button>
                        <button name="G1" onClick={() => clickedCell("G1")} className={shipInfo.includes("G1") ? "cellShip" : shipDamage.includes("G1") ? "cellDamaged" : "cell"}>G1</button>
                        <button name="G2" onClick={() => clickedCell("G2")} className={shipInfo.includes("G2") ? "cellShip" : shipDamage.includes("G2") ? "cellDamaged" : "cell"}>G2</button>
                        <button name="G3" onClick={() => clickedCell("G3")} className={shipInfo.includes("G3") ? "cellShip" : shipDamage.includes("G3") ? "cellDamaged" : "cell"}>G3</button>
                        <button name="G4" onClick={() => clickedCell("G4")} className={shipInfo.includes("G4") ? "cellShip" : shipDamage.includes("G4") ? "cellDamaged" : "cell"}>G4</button>
                        <button name="G5" onClick={() => clickedCell("G5")} className={shipInfo.includes("G5") ? "cellShip" : shipDamage.includes("G5") ? "cellDamaged" : "cell"}>G5</button>
                        <button name="G6" onClick={() => clickedCell("G6")} className={shipInfo.includes("G6") ? "cellShip" : shipDamage.includes("G6") ? "cellDamaged" : "cell"}>G6</button>
                        <button name="G7" onClick={() => clickedCell("G7")} className={shipInfo.includes("G7") ? "cellShip" : shipDamage.includes("G7") ? "cellDamaged" : "cell"}>G7</button>
                        <button name="G8" onClick={() => clickedCell("G8")} className={shipInfo.includes("G8") ? "cellShip" : shipDamage.includes("G8") ? "cellDamaged" : "cell"}>G8</button>
                        <button name="G9" onClick={() => clickedCell("G9")} className={shipInfo.includes("G9") ? "cellShip" : shipDamage.includes("G9") ? "cellDamaged" : "cell"}>G9</button>
                        <button name="G10" onClick={() => clickedCell("G10")} className={shipInfo.includes("G10") ? "cellShip" : shipDamage.includes("G10") ? "cellDamaged" : "cell"}>G10</button>

                        <button name="8" className="endCell">H</button>
                        <button name="H1" onClick={() => clickedCell("H1")} className={shipInfo.includes("H1") ? "cellShip" : shipDamage.includes("H1") ? "cellDamaged" : "cell"}>H1</button>
                        <button name="H2" onClick={() => clickedCell("H2")} className={shipInfo.includes("H2") ? "cellShip" : shipDamage.includes("H2") ? "cellDamaged" : "cell"}>H2</button>
                        <button name="H3" onClick={() => clickedCell("H3")} className={shipInfo.includes("H3") ? "cellShip" : shipDamage.includes("H3") ? "cellDamaged" : "cell"}>H3</button>
                        <button name="H4" onClick={() => clickedCell("H4")} className={shipInfo.includes("H4") ? "cellShip" : shipDamage.includes("H4") ? "cellDamaged" : "cell"}>H4</button>
                        <button name="H5" onClick={() => clickedCell("H5")} className={shipInfo.includes("H5") ? "cellShip" : shipDamage.includes("H5") ? "cellDamaged" : "cell"}>H5</button>
                        <button name="H6" onClick={() => clickedCell("H6")} className={shipInfo.includes("H6") ? "cellShip" : shipDamage.includes("H6") ? "cellDamaged" : "cell"}>H6</button>
                        <button name="H7" onClick={() => clickedCell("H7")} className={shipInfo.includes("H7") ? "cellShip" : shipDamage.includes("H7") ? "cellDamaged" : "cell"}>H7</button>
                        <button name="H8" onClick={() => clickedCell("H8")} className={shipInfo.includes("H8") ? "cellShip" : shipDamage.includes("H8") ? "cellDamaged" : "cell"}>H8</button>
                        <button name="H9" onClick={() => clickedCell("H9")} className={shipInfo.includes("H9") ? "cellShip" : shipDamage.includes("H9") ? "cellDamaged" : "cell"}>H9</button>
                        <button name="H10" onClick={() => clickedCell("H10")} className={shipInfo.includes("H10") ? "cellShip" : shipDamage.includes("H10") ? "cellDamaged" : "cell"}>H10</button>

                        <button name="9" className="endCell">I</button>
                        <button name="I1" onClick={() => clickedCell("I1")} className={shipInfo.includes("I1") ? "cellShip" : shipDamage.includes("I1") ? "cellDamaged" : "cell"}>I1</button>
                        <button name="I2" onClick={() => clickedCell("I2")} className={shipInfo.includes("I2") ? "cellShip" : shipDamage.includes("I2") ? "cellDamaged" : "cell"}>I2</button>
                        <button name="I3" onClick={() => clickedCell("I3")} className={shipInfo.includes("I3") ? "cellShip" : shipDamage.includes("I3") ? "cellDamaged" : "cell"}>I3</button>
                        <button name="I4" onClick={() => clickedCell("I4")} className={shipInfo.includes("I4") ? "cellShip" : shipDamage.includes("I4") ? "cellDamaged" : "cell"}>I4</button>
                        <button name="I5" onClick={() => clickedCell("I5")} className={shipInfo.includes("I5") ? "cellShip" : shipDamage.includes("I5") ? "cellDamaged" : "cell"}>I5</button>
                        <button name="I6" onClick={() => clickedCell("I6")} className={shipInfo.includes("I6") ? "cellShip" : shipDamage.includes("I6") ? "cellDamaged" : "cell"}>I6</button>
                        <button name="I7" onClick={() => clickedCell("I7")} className={shipInfo.includes("I7") ? "cellShip" : shipDamage.includes("I7") ? "cellDamaged" : "cell"}>I7</button>
                        <button name="I8" onClick={() => clickedCell("I8")} className={shipInfo.includes("I8") ? "cellShip" : shipDamage.includes("I8") ? "cellDamaged" : "cell"}>I8</button>
                        <button name="I9" onClick={() => clickedCell("I9")} className={shipInfo.includes("I9") ? "cellShip" : shipDamage.includes("I9") ? "cellDamaged" : "cell"}>I9</button>
                        <button name="I10" onClick={() => clickedCell("I10")} className={shipInfo.includes("I10") ? "cellShip" : shipDamage.includes("I10") ? "cellDamaged" : "cell"}>I10</button>


                        <button name="10" className="endCell">J</button>
                        <button name="J1" onClick={() => clickedCell("J1")} className={shipInfo.includes("J1") ? "cellShip" : shipDamage.includes("J1") ? "cellDamaged" : "cell"}>J1</button>
                        <button name="J2" onClick={() => clickedCell("J2")} className={shipInfo.includes("J2") ? "cellShip" : shipDamage.includes("J2") ? "cellDamaged" : "cell"}>J2</button>
                        <button name="J3" onClick={() => clickedCell("J3")} className={shipInfo.includes("J3") ? "cellShip" : shipDamage.includes("J3") ? "cellDamaged" : "cell"}>J3</button>
                        <button name="J4" onClick={() => clickedCell("J4")} className={shipInfo.includes("J4") ? "cellShip" : shipDamage.includes("J4") ? "cellDamaged" : "cell"}>J4</button>
                        <button name="J5" onClick={() => clickedCell("J5")} className={shipInfo.includes("J5") ? "cellShip" : shipDamage.includes("J5") ? "cellDamaged" : "cell"}>J5</button>
                        <button name="J6" onClick={() => clickedCell("J6")} className={shipInfo.includes("J6") ? "cellShip" : shipDamage.includes("J6") ? "cellDamaged" : "cell"}>J6</button>
                        <button name="J7" onClick={() => clickedCell("J7")} className={shipInfo.includes("J7") ? "cellShip" : shipDamage.includes("J7") ? "cellDamaged" : "cell"}>J7</button>
                        <button name="J8" onClick={() => clickedCell("J8")} className={shipInfo.includes("J8") ? "cellShip" : shipDamage.includes("J8") ? "cellDamaged" : "cell"}>J8</button>
                        <button name="J9" onClick={() => clickedCell("J9")} className={shipInfo.includes("J9") ? "cellShip" : shipDamage.includes("J9") ? "cellDamaged" : "cell"}>J9</button>
                        <button name="J10" onClick={() => clickedCell("J10")} className={shipInfo.includes("J10") ? "cellShip" : shipDamage.includes("J10") ? "cellDamaged" : "cell"}>J10</button>

                        <button name="end" className="endCellCorner">*</button>
                        <button name="1" className="endCell">1</button>
                        <button name="2" className="endCell">2</button>
                        <button name="3" className="endCell">3</button>
                        <button name="4" className="endCell">4</button>
                        <button name="5" className="endCell">5</button>
                        <button name="6" className="endCell">6</button>
                        <button name="7" className="endCell">7</button>
                        <button name="8" className="endCell">8</button>
                        <button name="9" className="endCell">9</button>
                        <button name="10" className="endCell">10</button>
                    </ul>
                </div >
                <div className="gameBoardRender2">
                    <h2>Player 2</h2>
                    <ul>
                        <button name="1" className="endCell">A</button>
                        <button name="A1" onClick={() => clickedEnemyCell("A1")} className={enemyShipInfo.includes("A1") ? "cellShip" : enemyShipDamage.includes("A1") ? "cellDamaged" : "cell"}>A1</button>
                        <button name="A2" onClick={() => clickedEnemyCell("A2")} className={enemyShipInfo.includes("A2") ? "cellShip" : enemyShipDamage.includes("A2") ? "cellDamaged" : "cell"}>A2</button>
                        <button name="A3" onClick={() => clickedEnemyCell("A3")} className={enemyShipInfo.includes("A3") ? "cellShip" : enemyShipDamage.includes("A3") ? "cellDamaged" : "cell"}>A3</button>
                        <button name="A4" onClick={() => clickedEnemyCell("A4")} className={enemyShipInfo.includes("A4") ? "cellShip" : enemyShipDamage.includes("A4") ? "cellDamaged" : "cell"}>A4</button>
                        <button name="A5" onClick={() => clickedEnemyCell("A5")} className={enemyShipInfo.includes("A5") ? "cellShip" : enemyShipDamage.includes("A5") ? "cellDamaged" : "cell"}>A5</button>
                        <button name="A6" onClick={() => clickedEnemyCell("A6")} className={enemyShipInfo.includes("A6") ? "cellShip" : enemyShipDamage.includes("A6") ? "cellDamaged" : "cell"}>A6</button>
                        <button name="A7" onClick={() => clickedEnemyCell("A7")} className={enemyShipInfo.includes("A7") ? "cellShip" : enemyShipDamage.includes("A7") ? "cellDamaged" : "cell"}>A7</button>
                        <button name="A8" onClick={() => clickedEnemyCell("A8")} className={enemyShipInfo.includes("A8") ? "cellShip" : enemyShipDamage.includes("A8") ? "cellDamaged" : "cell"}>A8</button>
                        <button name="A9" onClick={() => clickedEnemyCell("A9")} className={enemyShipInfo.includes("A9") ? "cellShip" : enemyShipDamage.includes("A9") ? "cellDamaged" : "cell"}>A9</button>
                        <button name="A10" onClick={() => clickedEnemyCell("A10")} className={enemyShipInfo.includes("A10") ? "cellShip" : enemyShipDamage.includes("A10") ? "cellDamaged" : "cell"}>A10</button>

                        <button name="2" className="endCell">B</button>
                        <button name="B1" onClick={() => clickedEnemyCell("B1")} className={enemyShipInfo.includes("B1") ? "cellShip" : enemyShipDamage.includes("B1") ? "cellDamaged" : "cell"}>B1</button>
                        <button name="B2" onClick={() => clickedEnemyCell("B2")} className={enemyShipInfo.includes("B2") ? "cellShip" : enemyShipDamage.includes("B2") ? "cellDamaged" : "cell"}>B2</button>
                        <button name="B3" onClick={() => clickedEnemyCell("B3")} className={enemyShipInfo.includes("B3") ? "cellShip" : enemyShipDamage.includes("B3") ? "cellDamaged" : "cell"}>B3</button>
                        <button name="B4" onClick={() => clickedEnemyCell("B4")} className={enemyShipInfo.includes("B4") ? "cellShip" : enemyShipDamage.includes("B4") ? "cellDamaged" : "cell"}>B4</button>
                        <button name="B5" onClick={() => clickedEnemyCell("B5")} className={enemyShipInfo.includes("B5") ? "cellShip" : enemyShipDamage.includes("B5") ? "cellDamaged" : "cell"}>B5</button>
                        <button name="B6" onClick={() => clickedEnemyCell("B6")} className={enemyShipInfo.includes("B6") ? "cellShip" : enemyShipDamage.includes("B6") ? "cellDamaged" : "cell"}>B6</button>
                        <button name="B7" onClick={() => clickedEnemyCell("B7")} className={enemyShipInfo.includes("B7") ? "cellShip" : enemyShipDamage.includes("B7") ? "cellDamaged" : "cell"}>B7</button>
                        <button name="B8" onClick={() => clickedEnemyCell("B8")} className={enemyShipInfo.includes("B8") ? "cellShip" : enemyShipDamage.includes("B8") ? "cellDamaged" : "cell"}>B8</button>
                        <button name="B9" onClick={() => clickedEnemyCell("B9")} className={enemyShipInfo.includes("B9") ? "cellShip" : enemyShipDamage.includes("B9") ? "cellDamaged" : "cell"}>B9</button>
                        <button name="B10" onClick={() => clickedEnemyCell("B10")} className={enemyShipInfo.includes("B10") ? "cellShip" : enemyShipDamage.includes("B10") ? "cellDamaged" : "cell"}>B10</button>

                        <button name="3" className="endCell">C</button>
                        <button name="C1" onClick={() => clickedEnemyCell("C1")} className={enemyShipInfo.includes("C1") ? "cellShip" : enemyShipDamage.includes("C1") ? "cellDamaged" : "cell"}>C1</button>
                        <button name="C2" onClick={() => clickedEnemyCell("C2")} className={enemyShipInfo.includes("C2") ? "cellShip" : enemyShipDamage.includes("C2") ? "cellDamaged" : "cell"}>C2</button>
                        <button name="C3" onClick={() => clickedEnemyCell("C3")} className={enemyShipInfo.includes("C3") ? "cellShip" : enemyShipDamage.includes("C3") ? "cellDamaged" : "cell"}>C3</button>
                        <button name="C4" onClick={() => clickedEnemyCell("C4")} className={enemyShipInfo.includes("C4") ? "cellShip" : enemyShipDamage.includes("C4") ? "cellDamaged" : "cell"}>C4</button>
                        <button name="C5" onClick={() => clickedEnemyCell("C5")} className={enemyShipInfo.includes("C5") ? "cellShip" : enemyShipDamage.includes("C5") ? "cellDamaged" : "cell"}>C5</button>
                        <button name="C6" onClick={() => clickedEnemyCell("C6")} className={enemyShipInfo.includes("C6") ? "cellShip" : enemyShipDamage.includes("C6") ? "cellDamaged" : "cell"}>C6</button>
                        <button name="C7" onClick={() => clickedEnemyCell("C7")} className={enemyShipInfo.includes("C7") ? "cellShip" : enemyShipDamage.includes("C7") ? "cellDamaged" : "cell"}>C7</button>
                        <button name="C8" onClick={() => clickedEnemyCell("C8")} className={enemyShipInfo.includes("C8") ? "cellShip" : enemyShipDamage.includes("C8") ? "cellDamaged" : "cell"}>C8</button>
                        <button name="C9" onClick={() => clickedEnemyCell("C9")} className={enemyShipInfo.includes("C9") ? "cellShip" : enemyShipDamage.includes("C9") ? "cellDamaged" : "cell"}>C9</button>
                        <button name="C10" onClick={() => clickedEnemyCell("C10")} className={enemyShipInfo.includes("C10") ? "cellShip" : enemyShipDamage.includes("C10") ? "cellDamaged" : "cell"}>C10</button>


                        <button name="4" className="endCell">D</button>
                        <button name="D1" onClick={() => clickedEnemyCell("D1")} className={enemyShipInfo.includes("D1") ? "cellShip" : enemyShipDamage.includes("D1") ? "cellDamaged" : "cell"}>D1</button>
                        <button name="D2" onClick={() => clickedEnemyCell("D2")} className={enemyShipInfo.includes("D2") ? "cellShip" : enemyShipDamage.includes("D2") ? "cellDamaged" : "cell"}>D2</button>
                        <button name="D3" onClick={() => clickedEnemyCell("D3")} className={enemyShipInfo.includes("D3") ? "cellShip" : enemyShipDamage.includes("D3") ? "cellDamaged" : "cell"}>D3</button>
                        <button name="D4" onClick={() => clickedEnemyCell("D4")} className={enemyShipInfo.includes("D4") ? "cellShip" : enemyShipDamage.includes("D4") ? "cellDamaged" : "cell"}>D4</button>
                        <button name="D5" onClick={() => clickedEnemyCell("D5")} className={enemyShipInfo.includes("D5") ? "cellShip" : enemyShipDamage.includes("D5") ? "cellDamaged" : "cell"}>D5</button>
                        <button name="D6" onClick={() => clickedEnemyCell("D6")} className={enemyShipInfo.includes("D6") ? "cellShip" : enemyShipDamage.includes("D6") ? "cellDamaged" : "cell"}>D6</button>
                        <button name="D7" onClick={() => clickedEnemyCell("D7")} className={enemyShipInfo.includes("D7") ? "cellShip" : enemyShipDamage.includes("D7") ? "cellDamaged" : "cell"}>D7</button>
                        <button name="D8" onClick={() => clickedEnemyCell("D8")} className={enemyShipInfo.includes("D8") ? "cellShip" : enemyShipDamage.includes("D8") ? "cellDamaged" : "cell"}>D8</button>
                        <button name="D9" onClick={() => clickedEnemyCell("D9")} className={enemyShipInfo.includes("D9") ? "cellShip" : enemyShipDamage.includes("D9") ? "cellDamaged" : "cell"}>D9</button>
                        <button name="D10" onClick={() => clickedEnemyCell("D10")} className={enemyShipInfo.includes("D10") ? "cellShip" : enemyShipDamage.includes("D10") ? "cellDamaged" : "cell"}>D10</button>

                        <button name="5" className="endCell">E</button>
                        <button name="E1" onClick={() => clickedEnemyCell("E1")} className={enemyShipInfo.includes("E1") ? "cellShip" : enemyShipDamage.includes("E1") ? "cellDamaged" : "cell"}>E1</button>
                        <button name="E2" onClick={() => clickedEnemyCell("E2")} className={enemyShipInfo.includes("E2") ? "cellShip" : enemyShipDamage.includes("E2") ? "cellDamaged" : "cell"}>E2</button>
                        <button name="E3" onClick={() => clickedEnemyCell("E3")} className={enemyShipInfo.includes("E3") ? "cellShip" : enemyShipDamage.includes("E3") ? "cellDamaged" : "cell"}>E3</button>
                        <button name="E4" onClick={() => clickedEnemyCell("E4")} className={enemyShipInfo.includes("E4") ? "cellShip" : enemyShipDamage.includes("E4") ? "cellDamaged" : "cell"}>E4</button>
                        <button name="E5" onClick={() => clickedEnemyCell("E5")} className={enemyShipInfo.includes("E5") ? "cellShip" : enemyShipDamage.includes("E5") ? "cellDamaged" : "cell"}>E5</button>
                        <button name="E6" onClick={() => clickedEnemyCell("E6")} className={enemyShipInfo.includes("E6") ? "cellShip" : enemyShipDamage.includes("E6") ? "cellDamaged" : "cell"}>E6</button>
                        <button name="E7" onClick={() => clickedEnemyCell("E7")} className={enemyShipInfo.includes("E7") ? "cellShip" : enemyShipDamage.includes("E7") ? "cellDamaged" : "cell"}>E7</button>
                        <button name="E8" onClick={() => clickedEnemyCell("E8")} className={enemyShipInfo.includes("E8") ? "cellShip" : enemyShipDamage.includes("E8") ? "cellDamaged" : "cell"}>E8</button>
                        <button name="E9" onClick={() => clickedEnemyCell("E9")} className={enemyShipInfo.includes("E9") ? "cellShip" : enemyShipDamage.includes("E9") ? "cellDamaged" : "cell"}>E9</button>
                        <button name="E10" onClick={() => clickedEnemyCell("E10")} className={enemyShipInfo.includes("E10") ? "cellShip" : enemyShipDamage.includes("E10") ? "cellDamaged" : "cell"}>E10</button>

                        <button name="6" className="endCell">F</button>
                        <button name="F1" onClick={() => clickedEnemyCell("F1")} className={enemyShipInfo.includes("F1") ? "cellShip" : enemyShipDamage.includes("F1") ? "cellDamaged" : "cell"}>F1</button>
                        <button name="F2" onClick={() => clickedEnemyCell("F2")} className={enemyShipInfo.includes("F2") ? "cellShip" : enemyShipDamage.includes("F2") ? "cellDamaged" : "cell"}>F2</button>
                        <button name="F3" onClick={() => clickedEnemyCell("F3")} className={enemyShipInfo.includes("F3") ? "cellShip" : enemyShipDamage.includes("F3") ? "cellDamaged" : "cell"}>F3</button>
                        <button name="F4" onClick={() => clickedEnemyCell("F4")} className={enemyShipInfo.includes("F4") ? "cellShip" : enemyShipDamage.includes("F4") ? "cellDamaged" : "cell"}>F4</button>
                        <button name="F5" onClick={() => clickedEnemyCell("F5")} className={enemyShipInfo.includes("F5") ? "cellShip" : enemyShipDamage.includes("F5") ? "cellDamaged" : "cell"}>F5</button>
                        <button name="F6" onClick={() => clickedEnemyCell("F6")} className={enemyShipInfo.includes("F6") ? "cellShip" : enemyShipDamage.includes("F6") ? "cellDamaged" : "cell"}>F6</button>
                        <button name="F7" onClick={() => clickedEnemyCell("F7")} className={enemyShipInfo.includes("F7") ? "cellShip" : enemyShipDamage.includes("F7") ? "cellDamaged" : "cell"}>F7</button>
                        <button name="F8" onClick={() => clickedEnemyCell("F8")} className={enemyShipInfo.includes("F8") ? "cellShip" : enemyShipDamage.includes("F8") ? "cellDamaged" : "cell"}>F8</button>
                        <button name="F9" onClick={() => clickedEnemyCell("F9")} className={enemyShipInfo.includes("F9") ? "cellShip" : enemyShipDamage.includes("F9") ? "cellDamaged" : "cell"}>F9</button>
                        <button name="F10" onClick={() => clickedEnemyCell("F10")} className={enemyShipInfo.includes("F10") ? "cellShip" : enemyShipDamage.includes("F10") ? "cellDamaged" : "cell"}>F10</button>

                        <button name="7" className="endCell">G</button>
                        <button name="G1" onClick={() => clickedEnemyCell("G1")} className={enemyShipInfo.includes("G1") ? "cellShip" : enemyShipDamage.includes("G1") ? "cellDamaged" : "cell"}>G1</button>
                        <button name="G2" onClick={() => clickedEnemyCell("G2")} className={enemyShipInfo.includes("G2") ? "cellShip" : enemyShipDamage.includes("G2") ? "cellDamaged" : "cell"}>G2</button>
                        <button name="G3" onClick={() => clickedEnemyCell("G3")} className={enemyShipInfo.includes("G3") ? "cellShip" : enemyShipDamage.includes("G3") ? "cellDamaged" : "cell"}>G3</button>
                        <button name="G4" onClick={() => clickedEnemyCell("G4")} className={enemyShipInfo.includes("G4") ? "cellShip" : enemyShipDamage.includes("G4") ? "cellDamaged" : "cell"}>G4</button>
                        <button name="G5" onClick={() => clickedEnemyCell("G5")} className={enemyShipInfo.includes("G5") ? "cellShip" : enemyShipDamage.includes("G5") ? "cellDamaged" : "cell"}>G5</button>
                        <button name="G6" onClick={() => clickedEnemyCell("G6")} className={enemyShipInfo.includes("G6") ? "cellShip" : enemyShipDamage.includes("G6") ? "cellDamaged" : "cell"}>G6</button>
                        <button name="G7" onClick={() => clickedEnemyCell("G7")} className={enemyShipInfo.includes("G7") ? "cellShip" : enemyShipDamage.includes("G7") ? "cellDamaged" : "cell"}>G7</button>
                        <button name="G8" onClick={() => clickedEnemyCell("G8")} className={enemyShipInfo.includes("G8") ? "cellShip" : enemyShipDamage.includes("G8") ? "cellDamaged" : "cell"}>G8</button>
                        <button name="G9" onClick={() => clickedEnemyCell("G9")} className={enemyShipInfo.includes("G9") ? "cellShip" : enemyShipDamage.includes("G9") ? "cellDamaged" : "cell"}>G9</button>
                        <button name="G10" onClick={() => clickedEnemyCell("G10")} className={enemyShipInfo.includes("G10") ? "cellShip" : enemyShipDamage.includes("G10") ? "cellDamaged" : "cell"}>G10</button>

                        <button name="8" className="endCell">H</button>
                        <button name="H1" onClick={() => clickedEnemyCell("H1")} className={enemyShipInfo.includes("H1") ? "cellShip" : enemyShipDamage.includes("H1") ? "cellDamaged" : "cell"}>H1</button>
                        <button name="H2" onClick={() => clickedEnemyCell("H2")} className={enemyShipInfo.includes("H2") ? "cellShip" : enemyShipDamage.includes("H2") ? "cellDamaged" : "cell"}>H2</button>
                        <button name="H3" onClick={() => clickedEnemyCell("H3")} className={enemyShipInfo.includes("H3") ? "cellShip" : enemyShipDamage.includes("H3") ? "cellDamaged" : "cell"}>H3</button>
                        <button name="H4" onClick={() => clickedEnemyCell("H4")} className={enemyShipInfo.includes("H4") ? "cellShip" : enemyShipDamage.includes("H4") ? "cellDamaged" : "cell"}>H4</button>
                        <button name="H5" onClick={() => clickedEnemyCell("H5")} className={enemyShipInfo.includes("H5") ? "cellShip" : enemyShipDamage.includes("H5") ? "cellDamaged" : "cell"}>H5</button>
                        <button name="H6" onClick={() => clickedEnemyCell("H6")} className={enemyShipInfo.includes("H6") ? "cellShip" : enemyShipDamage.includes("H6") ? "cellDamaged" : "cell"}>H6</button>
                        <button name="H7" onClick={() => clickedEnemyCell("H7")} className={enemyShipInfo.includes("H7") ? "cellShip" : enemyShipDamage.includes("H7") ? "cellDamaged" : "cell"}>H7</button>
                        <button name="H8" onClick={() => clickedEnemyCell("H8")} className={enemyShipInfo.includes("H8") ? "cellShip" : enemyShipDamage.includes("H8") ? "cellDamaged" : "cell"}>H8</button>
                        <button name="H9" onClick={() => clickedEnemyCell("H9")} className={enemyShipInfo.includes("H9") ? "cellShip" : enemyShipDamage.includes("H9") ? "cellDamaged" : "cell"}>H9</button>
                        <button name="H10" onClick={() => clickedEnemyCell("H10")} className={enemyShipInfo.includes("H10") ? "cellShip" : enemyShipDamage.includes("H10") ? "cellDamaged" : "cell"}>H10</button>

                        <button name="9" className="endCell">I</button>
                        <button name="I1" onClick={() => clickedEnemyCell("I1")} className={enemyShipInfo.includes("I1") ? "cellShip" : enemyShipDamage.includes("I1") ? "cellDamaged" : "cell"}>I1</button>
                        <button name="I2" onClick={() => clickedEnemyCell("I2")} className={enemyShipInfo.includes("I2") ? "cellShip" : enemyShipDamage.includes("I2") ? "cellDamaged" : "cell"}>I2</button>
                        <button name="I3" onClick={() => clickedEnemyCell("I3")} className={enemyShipInfo.includes("I3") ? "cellShip" : enemyShipDamage.includes("I3") ? "cellDamaged" : "cell"}>I3</button>
                        <button name="I4" onClick={() => clickedEnemyCell("I4")} className={enemyShipInfo.includes("I4") ? "cellShip" : enemyShipDamage.includes("I4") ? "cellDamaged" : "cell"}>I4</button>
                        <button name="I5" onClick={() => clickedEnemyCell("I5")} className={enemyShipInfo.includes("I5") ? "cellShip" : enemyShipDamage.includes("I5") ? "cellDamaged" : "cell"}>I5</button>
                        <button name="I6" onClick={() => clickedEnemyCell("I6")} className={enemyShipInfo.includes("I6") ? "cellShip" : enemyShipDamage.includes("I6") ? "cellDamaged" : "cell"}>I6</button>
                        <button name="I7" onClick={() => clickedEnemyCell("I7")} className={enemyShipInfo.includes("I7") ? "cellShip" : enemyShipDamage.includes("I7") ? "cellDamaged" : "cell"}>I7</button>
                        <button name="I8" onClick={() => clickedEnemyCell("I8")} className={enemyShipInfo.includes("I8") ? "cellShip" : enemyShipDamage.includes("I8") ? "cellDamaged" : "cell"}>I8</button>
                        <button name="I9" onClick={() => clickedEnemyCell("I9")} className={enemyShipInfo.includes("I9") ? "cellShip" : enemyShipDamage.includes("I9") ? "cellDamaged" : "cell"}>I9</button>
                        <button name="I10" onClick={() => clickedEnemyCell("I10")} className={enemyShipInfo.includes("I10") ? "cellShip" : enemyShipDamage.includes("I10") ? "cellDamaged" : "cell"}>I10</button>


                        <button name="10" className="endCell">J</button>
                        <button name="J1" onClick={() => clickedEnemyCell("J1")} className={enemyShipInfo.includes("J1") ? "cellShip" : enemyShipDamage.includes("J1") ? "cellDamaged" : "cell"}>J1</button>
                        <button name="J2" onClick={() => clickedEnemyCell("J2")} className={enemyShipInfo.includes("J2") ? "cellShip" : enemyShipDamage.includes("J2") ? "cellDamaged" : "cell"}>J2</button>
                        <button name="J3" onClick={() => clickedEnemyCell("J3")} className={enemyShipInfo.includes("J3") ? "cellShip" : enemyShipDamage.includes("J3") ? "cellDamaged" : "cell"}>J3</button>
                        <button name="J4" onClick={() => clickedEnemyCell("J4")} className={enemyShipInfo.includes("J4") ? "cellShip" : enemyShipDamage.includes("J4") ? "cellDamaged" : "cell"}>J4</button>
                        <button name="J5" onClick={() => clickedEnemyCell("J5")} className={enemyShipInfo.includes("J5") ? "cellShip" : enemyShipDamage.includes("J5") ? "cellDamaged" : "cell"}>J5</button>
                        <button name="J6" onClick={() => clickedEnemyCell("J6")} className={enemyShipInfo.includes("J6") ? "cellShip" : enemyShipDamage.includes("J6") ? "cellDamaged" : "cell"}>J6</button>
                        <button name="J7" onClick={() => clickedEnemyCell("J7")} className={enemyShipInfo.includes("J7") ? "cellShip" : enemyShipDamage.includes("J7") ? "cellDamaged" : "cell"}>J7</button>
                        <button name="J8" onClick={() => clickedEnemyCell("J8")} className={enemyShipInfo.includes("J8") ? "cellShip" : enemyShipDamage.includes("J8") ? "cellDamaged" : "cell"}>J8</button>
                        <button name="J9" onClick={() => clickedEnemyCell("J9")} className={enemyShipInfo.includes("J9") ? "cellShip" : enemyShipDamage.includes("J9") ? "cellDamaged" : "cell"}>J9</button>
                        <button name="J10" onClick={() => clickedEnemyCell("J10")} className={enemyShipInfo.includes("J10") ? "cellShip" : enemyShipDamage.includes("J10") ? "cellDamaged" : "cell"}>J10</button>

                        <button name="end" className="endCellCorner">*</button>
                        <button name="1" className="endCell">1</button>
                        <button name="2" className="endCell">2</button>
                        <button name="3" className="endCell">3</button>
                        <button name="4" className="endCell">4</button>
                        <button name="5" className="endCell">5</button>
                        <button name="6" className="endCell">6</button>
                        <button name="7" className="endCell">7</button>
                        <button name="8" className="endCell">8</button>
                        <button name="9" className="endCell">9</button>
                        <button name="10" className="endCell">10</button>
                    </ul>
                </div>
            </div>
        </>
    )
}

export default GameBoard
