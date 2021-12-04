public class Eagle {
    static int size = 5;
    static int eagleSpeed = -2;
    static int[] heightRange = {10, 20};

    public int pos;
    public int height;
    public boolean isAlive;

    Eagle(int pos, int height) {
        this.pos = pos;
        this.height = height;
        this.isAlive = true;
    }

    void step() {
        this.pos += eagleSpeed;
    }

    int distanceToBird(Bird bird) {
        return this.pos - bird.posX;
    }

    @Override
    public String toString() {
        return "Eagle{" +
                "pos=" + pos +
                ", height=" + height +
                ", isAlive=" + isAlive +
                '}';
    }
}
