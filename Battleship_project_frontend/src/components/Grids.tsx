
interface StompClient {
    send(destination: string, headers?: Record<string, string>, body?: string): void;
}
interface Props {
    shipInfo: string[];
    shipDamage: string[];
    enemyShipInfo: string[];
    enemyShipDamage: string[];
    stompClient: StompClient;
    savedName: string;
}

const Grids: React.FC<Props> = ({ savedName, shipInfo, shipDamage, enemyShipInfo, enemyShipDamage, stompClient }) => {

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
        console.log(value)
        stompClient.send("/app/gameData", {}, JSON.stringify(value));
    }

    const clickedEnemyCell = (value: string) => {
        console.log(value)
    }

    return (
        <>
            <div className="gameBoardOuterGreater">
                <div className="gameBoardOuter">
                    <div className="gameBoardRender">
                        <h2>{savedName}</h2>
                        <ul>
                            {populateGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                        </ul>
                    </div >
                    <div className="gameBoardRender2">
                        <h2>Player 2</h2>
                        <ul>
                            {populateEnemyGrid()}
                            <button name="end" className="endCellCorner">*</button>
                            {numbersBottom()}
                        </ul>
                    </div>
                </div >
            </div>
        </>
    )
}

export default Grids