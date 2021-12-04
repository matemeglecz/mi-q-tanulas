import java.util.List;

public class InfoDTO {
    public int mapWidth;
    public int birdX;
    public int birdY;
    public int birdSize;
    public List<Tube> tubes;
    public int tubeWidth;
    public int tubeGapsize;
    public List<Eagle> eagles;
    public int eagleSize;
    public boolean shotFired;

    public InfoDTO() {
    }

    public InfoDTO(int mapWidth, int birdX, int birdY, int birdSize, List<Tube> tubes, int tubeWidth, int tubeGapsize,
                   List<Eagle> eagles, int eagleSize, boolean shotFired) {
        this.mapWidth = mapWidth;
        this.birdX = birdX;
        this.birdY = birdY;
        this.birdSize = birdSize;
        this.tubes = tubes;
        this.tubeWidth = tubeWidth;
        this.tubeGapsize = tubeGapsize;
        this.eagles = eagles;
        this.eagleSize = eagleSize;
        this.shotFired = shotFired;
    }

    @Override
    public String toString() {
        return "InfoDTO{" +
                "birdX=" + birdX +
                ", birdY=" + birdY +
                ", birdSize=" + birdSize +
                ", tubes=" + tubes +
                ", tubeWidth=" + tubeWidth +
                ", tubeGapsize=" + tubeGapsize +
                ", eagles=" + eagles +
                ", eagleSize=" + eagleSize +
                ", shotFired=" + shotFired +
                '}';
    }
}
