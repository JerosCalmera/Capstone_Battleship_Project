import { ReactComponentElement, useEffect, useState } from "react";

interface StompClient {
    send(destination: string, headers?: Record<string, string>, body?: string): void;
}
interface Props {
    shipInfo: string;
    shipDamage: string;
    enemyShipInfo: string;
    enemyShipDamage: string;
    stompClient: StompClient;
    savedName: string;
    player1Data: string;
    player2Data: string;
    carrier: number;
    battleship: number;
    cruiser: number;
    destroyer: number;
}

const Grids: React.FC<Props> = ({ destroyer, cruiser, battleship, carrier, player1Data, player2Data, savedName, shipInfo, shipDamage, enemyShipInfo, enemyShipDamage, stompClient }) => {

    const [shipPlacement, setShipPlacement] = useState<boolean>(false)
    const [shipSize, setShipSize] = useState<number>(0)


    const populateGrid = () => {
        const letter: Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        const value: Array<string> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        const label: Array<string> = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
        const end = [];
        for (let i = 0; i < 10; i++) {
            const buttons = [];
            for (let j = 0; j < 10; j++) {
                const cellValue: any = letter[i] + value[j];
                buttons.push(
                    <button key={cellValue}
                        onClick={() => clickedCell(cellValue)}
                        className={shipInfo.includes(cellValue) ? "cellShip" : shipDamage.includes(cellValue) ? "cellDamaged" : "cell"
                        }>**</button>);
            }
            end.push(
                <div>
                    <button className="endCell">
                        {letter[i]}
                    </button>
                    {buttons}
                </div>)
        }
        return end;
    }

    const populateEnemyGrid = () => {
        const letter: Array<string> = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
        const value: Array<string> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
        const label: Array<string> = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"]
        const end = [];
        for (let i = 0; i < 10; i++) {
            const buttons = [];
            for (let j = 0; j < 10; j++) {
                const cellValue: any = letter[i] + value[j];
                buttons.push(
                    <button key={cellValue}
                        onClick={() => clickedEnemyCell(cellValue)}
                        className={enemyShipInfo.includes(cellValue) ? "cellShip" : enemyShipDamage.includes(cellValue) ? "cellDamaged" : "cell"
                        }>**</button>);
            }
            end.push(
                <div>
                    <button className="endCell">
                        {letter[i]}
                    </button>
                    {buttons}
                </div>)
        }
        return end;
    }
    const numbersBottom = () => {
        const numbers = [];
        for (let i = 0; i < 10; i++) {
            numbers.push(
                <button className="endCell">{i + 1}</button>
            )
        }
        return numbers
    }

    const clickedCell = (value: string) => {
        if (shipPlacement === true) {
            stompClient.send("/app/gameData", {}, JSON.stringify(value));
        }
        else { stompClient.send("/app/placement", {}, JSON.stringify(value + shipSize + savedName)); }

    }

    const clickedEnemyCell = (value: string) => {
        console.log(value)
    }


    // const shipPlaced = (value: string) => {
    //     stompClient.send("/app/placement", {}, JSON.stringify(value + shipSize + savedName));
    // }

    return (
        <>
            <div className="gameBoardOuterGreater">
                <div className="gameBoardOuter">
                    <div className="shipPlacementOuter">
                        <div className="shipPlacementInner">
                            {carrier > 0 ?
                                <ul>
                                    <button onClick={() => setShipSize(5)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(5)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(5)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(5)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(5)} className="shipToPlace">*</button>
                                    <h4>Carrier: x{carrier}</h4><br />
                                </ul>
                                : null}
                            {battleship > 0 ?
                                <ul>
                                    <button onClick={() => setShipSize(4)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(4)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(4)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(4)} className="shipToPlace">*</button>
                                    <h4>Battleship: x{battleship}</h4><br />
                                </ul>
                                : null}
                            {cruiser > 0 ?
                                <ul>
                                    <button onClick={() => setShipSize(3)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(3)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(3)} className="shipToPlace">*</button>
                                    <h4>Cruiser: x{cruiser}</h4> <br />
                                </ul>
                                : null}
                            {destroyer > 0 ?
                                <ul>
                                    <button onClick={() => setShipSize(2)} className="shipToPlace">*</button>
                                    <button onClick={() => setShipSize(2)} className="shipToPlace">*</button>
                                    <h4>Destroyer: x{destroyer}</h4><br />
                                </ul>
                                : null}
                        </div>
                    </div>
                    <div className="gameBoardRender">
                        <h2>{player1Data}</h2>
                        <ul>
                            {populateGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                        </ul>
                    </div >
                    <div className="gameBoardRender2">
                        <h2>{player2Data}</h2>
                        <ul>
                            {populateEnemyGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                        </ul>
                    </div>
                </div >
            </div >
        </>
    )
}

export default Grids