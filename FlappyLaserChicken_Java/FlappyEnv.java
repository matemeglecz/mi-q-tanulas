import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FlappyEnv {
    int seed = 123;
    public static int mapSizeX = 80;
    public static int mapSizeY = 40;
    int objectInterval = 30;
    int gravity = 1;
    int jumpForce = -5;
    Random rnd;
    Bird bird;
    int stepCounter;
    List<Tube> tubes = new ArrayList<>();
    boolean done;
    static double eagleProbability = 0.5;
    List<Eagle> eagles = new ArrayList<>();

    public FlappyEnv() {
//        Random seedRnd = new Random();
//        seed = seedRnd.nextInt(200);
        reset();
    }

    public FlappyEnv(int seed) {
       this.seed = seed;
        reset();
    }

    public int[] getActionSpace() {
        return new int[]{0, 1, 2};
    }
    public int[][] getObservationSpace() {
        return new int[][]{
                {-1, mapSizeY - Bird.size + 1},
                {-Bird.maxSpeed, Bird.maxSpeed},
                {0, 1},
                {Math.min(-(Tube.width + 1), -(Eagle.size + 1)), mapSizeX},
                {Math.min(Tube.heightRange[0], Eagle.heightRange[0]),
                        Math.max(Tube.heightRange[1], Eagle.heightRange[1])}
        };
    }
    public int[] getObservationSpaceSize() {
        int[][] observationSpace = getObservationSpace();
        return new int[] {
                observationSpace[0][1] - observationSpace[0][0] +1,
                observationSpace[1][1] - observationSpace[1][0] +1,
                observationSpace[2][1] - observationSpace[2][0] +1,
                observationSpace[3][1] - observationSpace[3][0] +1,
                observationSpace[4][1] - observationSpace[4][0] +1
        };
    }

    public FlappyAgent.StateDTO state() {
        int currentBirdPos;
        if(this.bird.posY < 0) currentBirdPos = -1;
        else if(this.bird.posY > mapSizeY - Bird.size) currentBirdPos = mapSizeY - Bird.size + 1;
        else currentBirdPos = this.bird.posY;

        currentBirdPos+= 1;
        int currentBirdVel = this.bird.speed + Bird.maxSpeed;

        int closestObjectType = 0;
        int currentObjectDistance = mapSizeX;
        int currentObjectHeight = 0;

        if (!this.tubes.isEmpty()) {
            Tube lastTube = this.tubes.stream().sorted(Comparator.comparingInt(o -> o.pos))
                    .collect(Collectors.toList()).get(0);
            // closestObjectType = 0;
            currentObjectDistance = lastTube.distanceToBird(this.bird) + Tube.width + 1;
            currentObjectHeight = lastTube.height - Tube.heightRange[0];
        }
        if (!this.eagles.isEmpty()) {
            Eagle lastEagle = this.eagles.stream().sorted(Comparator.comparingInt(o -> o.pos))
                    .collect(Collectors.toList()).get(0);
            if (lastEagle.pos < currentObjectDistance) {
                closestObjectType = 1;
                currentObjectDistance = lastEagle.distanceToBird(this.bird) + Eagle.size + 1;
                currentObjectHeight = lastEagle.height - Eagle.heightRange[0];
            }
        }

        return new FlappyAgent.StateDTO(currentBirdPos, currentBirdVel, closestObjectType,
                                        currentObjectDistance, currentObjectHeight);
    }

    private void spawnObject() {
        if (rnd.nextDouble() < eagleProbability) {
            int eagleHeight = this.rnd.nextInt(Eagle.heightRange[1] - Eagle.heightRange[0]) + Eagle.heightRange[0];
            this.eagles.add(new Eagle(mapSizeX, eagleHeight));
        }
        else {
            int tubeHeight = this.rnd.nextInt(Tube.heightRange[1] - Tube.heightRange[0]) + Tube.heightRange[0];
            this.tubes.add(new Tube(mapSizeX, tubeHeight));
        }
    }

    private boolean validAction(int action) {
        for(int a : getActionSpace()) {
            if(a == action)
                return true;
        }
        return false;
    }

    public ObservationDTO step(int action) {
        if(!validAction(action)) {
            return null;
        }
        double reward = 0.0;
        int shot_height = Integer.MIN_VALUE;
        if(!this.done) {
            this.stepCounter += 1;
            if(action == 0) {
                this.bird.step(gravity, true);
            }
            else if(action == 1) {
                this.bird.step(jumpForce, false);
            }
            else if(action == 2) {
                shot_height = this.bird.posY;
                this.bird.step(gravity, true);
            }

            if(this.stepCounter % objectInterval == 0) {
                spawnObject();
            }

            for(Tube t: this.tubes) {
                t.step();

                if(t.pos + Tube.width < this.bird.posX && !t.scored) {
                    t.scored = true;
                    reward = 1.0;
                }
            }
            this.tubes = this.tubes.stream().filter(tube -> !tube.scored).collect(Collectors.toList());

            if (shot_height > Integer.MIN_VALUE) {
                boolean hit = false;
                if (!this.eagles.isEmpty()) {
                    Eagle lastEagle = this.eagles.stream().sorted(Comparator.comparingInt(o -> o.pos))
                            .collect(Collectors.toList()).get(0);
                    if (shot_height < (lastEagle.height + Bird.size)
                            && shot_height > (lastEagle.height - (Eagle.size + Bird.size))) {
                        lastEagle.isAlive = false;
                        hit = true;
                        reward = 1.0;
                    }
                }
                if (!hit) {
                    reward = -0.25;
                }
            }

            for (Eagle eagle: this.eagles) {
                eagle.step();
            }

            this.eagles = this.eagles.stream().filter(eagle -> eagle.isAlive).collect(Collectors.toList());

            if(this.bird.checkCollide(this.tubes, this.eagles)) {
                reward = -1.0;
                this.done = true;
            }
        }

        boolean shotFired = shot_height > Integer.MIN_VALUE;

        InfoDTO info = new InfoDTO(FlappyEnv.mapSizeX, this.bird.posX, this.bird.posY, Bird.size, this.tubes,
                Tube.width, Tube.gapSize, this.eagles, Eagle.size, shotFired);
        ObservationDTO result = new ObservationDTO(state(), reward, done, info);

        return result;
    }

    public ObservationDTO reset() {
        //this.rnd = new Random(seed);
        this.rnd = new Random();
        this.bird = new Bird(10, mapSizeY / 2);
        this.stepCounter = 0;
        this.tubes.clear();
        this.eagles.clear();
        spawnObject();
        this.done = false;

        InfoDTO info = new InfoDTO(FlappyEnv.mapSizeX, this.bird.posX, this.bird.posY, Bird.size, this.tubes,
                Tube.width, Tube.gapSize, this.eagles, Eagle.size, false);
        ObservationDTO result = new ObservationDTO(state(), 0, done, info);
        return result;
    }
}
