public class FlappyAgent {
    public static class StateDTO {
        public int birdPos;
        public int birdSpeed;
        public int objectType;
        public int objectDistance;
        public int objectHeight;

        public StateDTO(int birdPos, int birdSpeed, int objectType, int objectDistance, int objectHeight) {
            this.birdPos = birdPos;
            this.birdSpeed = birdSpeed;
            this.objectType = objectType;
            this.objectDistance = objectDistance;
            this.objectHeight = objectHeight;
        }

        @Override
        public String toString() {
            return "StateDTO{" +
                    "birdPos=" + birdPos +
                    ", birdSpeed=" + birdSpeed +
                    ", objectType=" + objectType +
                    ", objectDistance=" + objectDistance +
                    ", objectHeight=" + objectHeight +
                    '}';
        }
    }
    public static class QTable implements java.io.Serializable {
        public double[][][][][][] table;

        public QTable() {

        }

        public QTable(int[] stateSpaceSize, int actionDimension) {
            table = new double[stateSpaceSize[0]][stateSpaceSize[1]][stateSpaceSize[2]][stateSpaceSize[3]][stateSpaceSize[4]][actionDimension];
        }

        public double[] getActions(StateDTO state) {
            return table[state.birdPos][state.birdSpeed][state.objectType][state.objectDistance][state.objectHeight];
        }

        public QTable copy() {
            QTable res = new QTable();
            res.table = this.table.clone();
            return res;
        }
    }

    QTable qTable;
    int[] actionSpace;
    int nIterations;
    double explore = 0.5;
    double exploit = 0.5;

    boolean test = false;

    public FlappyAgent(int[] observationSpaceSize, int[] actionSpace, int nIterations) {
        this.qTable = new QTable(observationSpaceSize,actionSpace.length);
        this.actionSpace = actionSpace;
        this.nIterations = nIterations;
    }

    public int step(StateDTO state) {
        int action = 0; //

        double[] actions = qTable.getActions(state);
        double maxQ = actions[0];
        for(int i = 0; i < actions.length; i++){
            if(actions[i] > maxQ) {
                maxQ = actions[i];
                action = i;
            }
        }

        return action;
    }
    int bestRewardSum = 0;
    QTable bestQtable;
    public void epochEnd(int epochRewardSum) {
        if(bestQtable == null) bestQtable = qTable.copy();
        if(epochRewardSum < bestRewardSum)
            qTable = bestQtable.copy();
        else {
            bestQtable = qTable.copy();
            bestRewardSum = epochRewardSum;
        }
    }



    public void learn(StateDTO oldState, int action, StateDTO newState, double reward) {
         double newQ= qTable.getActions(oldState)[action] + explore *(reward + exploit * getQMax(newState) - qTable.getActions(oldState)[action]);
         qTable.table[oldState.birdPos][oldState.birdSpeed][oldState.objectType][oldState.objectDistance][oldState.objectHeight][action] = newQ;
    }

    public void trainEnd() {
        /* ... */

        //qTable = null; //
        test = true;
    }

    private double getQMax(StateDTO state){
        double[] actions = qTable.getActions(state);
        double maxQ = actions[0];
        for (double action : actions) {
            if (action > maxQ) {
                maxQ = action;
            }
        }
        return maxQ;
    }
}
