package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.Marks;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 21/03/2016.
 */
public class RaceCourse {
    private double windSpeed;
    private BoatType boatType;
    private double boatLength;
    private double boatVMGUpwind;
    private double boatVMGReach;
    private double boatVMGRun;
    private int numOfBoats;//TODO Get all parameters in constructor.
    private int goalTime;
    private boolean isUpdated; //TODO remove
    private RaceCourseType raceCourseType;
    private Marks marks = new Marks();
    private static Dictionary<String,RaceCourseType> raceCourseTypes = new Hashtable<>();
    private UUID uuid;

    private List<AviObject> courseMarks = new ArrayList<AviObject>();
    private int windDir;
    private double distMark1;
    private AviLocation signalBoatLoc;
    private int courseType;
    private double startLineLength;
    private AviLocation referencePoint;


    public RaceCourse(){
        uuid = UUID.randomUUID();
    }



    public enum RaceCourseType{
        WINDWARD_LEEWARD_TRIANGLE,
        WINDWARD_LEEWARD,
        TRAPEZOID
    }
    public enum BoatType{ //TODO move to online database
        C470, C420, LASERRADIAL, LASERSTANDARD, LASER47, OPTIMISTINT, OPTIMISTLOCAL, RSX, BIC78, BIC68, BIC5, KITE, CATAMARAN17, CATAMARAN21, YACHT
    }

    public void calculateCourse(RaceCourseType rct){ //TODO calculate everything automatically always
        courseMarks.clear();
        SetRaceCourse(courseType);
    }

    /***
     * Find the midposition of the start line
     * @param signalBoatLoc
     * @param startLineLength
     * @return
     */
    private AviLocation findReferencePoint(AviLocation signalBoatLoc, int startLineLength) {
        return GeoUtils.toAviLocation(GeoUtils.getLocationFromDirDist(GeoUtils.toLocation(signalBoatLoc), (float) GeoUtils.relativeToTrueDirection(windDir, -90), (startLineLength / 2)));
    }

    private void SetRaceCourse(int courseType) {
        //set start line
        courseMarks.add(new AviObject("pin end",new AviLocation(signalBoatLoc, windDir-90, startLineLength), ObjectTypes.FlagBuoy , "red", uuid));
        //set reference point
        referencePoint=new AviLocation(new AviLocation(signalBoatLoc, courseMarks.get(0).getAviLocation()), windDir, 0.05);  //0.05 nm windward from the center of the start line
        //set gate 4
        //set mark 1
        courseMarks.add(new AviObject("Mark 1",new AviLocation(referencePoint, windDir , distMark1), ObjectTypes.Buoy , "orange", uuid));
        switch (courseType%10){ //race course general type
            case 1: // Trapezoid 60,120
                switch (courseType/10 %10){  //shorted or 1/2 or 2/3
                    case 0:  //shorted OUT, reach=1/2 beat
                        //set mark 2
                        courseMarks.add(new AviObject("Mark 2",new AviLocation(referencePoint, windDir-30 , courseMarks.get(1).getAviLocation() , windDir-120), ObjectTypes.Buoy , "orange", uuid));
                        //set mark 3
                        courseMarks.add(new AviObject("Mark 3",new AviLocation(referencePoint, windDir-60 , courseMarks.get(2).getAviLocation() , windDir-180), ObjectTypes.Buoy , "orange", uuid));
                        break;
                    case 1:  // equal beats, reach=1/2 beat
                        //set mark 2
                        courseMarks.add(new AviObject("Mark 2",new AviLocation(referencePoint, windDir-30 , courseMarks.get(2).getAviLocation() , windDir-120), ObjectTypes.Buoy , "orange", uuid));
                        //set mark 3
                        courseMarks.add(new AviObject("Mark 3",new AviLocation(referencePoint, windDir-120 , courseMarks.get(2).getAviLocation() , windDir-180), ObjectTypes.Buoy , "orange", uuid));
                        //set finish line
                        addGate(courseMarks, new AviObject("Finish",new AviLocation(courseMarks.get(3).getAviLocation(), windDir+110 , 0.1), ObjectTypes.FinishLine , "Blue", uuid, 0.001, 160));

                        break;
                    case 2: // equal beats, reach=2/3 beat
                        //set mark 2
                        courseMarks.add(new AviObject("Mark 2",new AviLocation(referencePoint, windDir-41 , courseMarks.get(2).getAviLocation() , windDir-120), ObjectTypes.Buoy , "orange", uuid));
                        //set mark 3
                        courseMarks.add(new AviObject("Mark 3", new AviLocation(referencePoint, windDir - 120, courseMarks.get(2).getAviLocation(), windDir - 180), ObjectTypes.Buoy, "orange", uuid));
                        //set finish line
                        addGate(courseMarks, new AviObject("Finish",new AviLocation(courseMarks.get(3).getAviLocation(), windDir+120 , 0.1), ObjectTypes.FinishLine , "Blue", uuid, 0.001, 160));
                        break;
                }
                break;
            case 2: // Trapezoid 70,110
                switch (courseType/10 %10){  //shorted or 1/2 or 2/3
                    case 0:  //shorted OUT, reach=1/2 beat
                        //set mark 2
                        courseMarks.add(new AviObject("Mark 2",new AviLocation(referencePoint, windDir-30 , courseMarks.get(1).getAviLocation() , windDir-110), ObjectTypes.Buoy , "orange", uuid));
                        //set mark 3
                        courseMarks.add(new AviObject("Mark 3",new AviLocation(referencePoint, windDir-70 , courseMarks.get(2).getAviLocation() , windDir-180), ObjectTypes.Buoy , "orange", uuid));
                        break;
                    case 1:  // equal beats, reach=1/2 beat
                        //set mark 2
                        courseMarks.add(new AviObject("Mark 2",new AviLocation(referencePoint, windDir-30 , courseMarks.get(2).getAviLocation() , windDir-110), ObjectTypes.Buoy , "orange", uuid));
                        //set mark 3
                        courseMarks.add(new AviObject("Mark 3",new AviLocation(referencePoint, windDir-110 , courseMarks.get(2).getAviLocation() , windDir-180), ObjectTypes.Buoy , "orange", uuid));
                        //set finish line
                        addGate(courseMarks, new AviObject("Finish",new AviLocation(courseMarks.get(3).getAviLocation(), windDir+110 , 0.1), ObjectTypes.FinishLine , "Blue", uuid, 0.001, 160));

                        break;
                    case 2: // equal beats, reach=2/3 beat
                        //set mark 2
                        courseMarks.add(new AviObject("Mark 2",new AviLocation(referencePoint, windDir-39 , courseMarks.get(2).getAviLocation() , windDir-110), ObjectTypes.Buoy , "orange", uuid));
                        //set mark 3
                        courseMarks.add(new AviObject("Mark 3", new AviLocation(referencePoint, windDir - 110, courseMarks.get(2).getAviLocation(), windDir - 180), ObjectTypes.Buoy, "orange", uuid));
                        //set finish line
                        addGate(courseMarks, new AviObject("Finish",new AviLocation(courseMarks.get(3).getAviLocation(), windDir+110 , 0.1), ObjectTypes.FinishLine , "Blue", uuid, 0.001, 160));
                        break;
                }
                break;
            case 3:  //windward-leeward
                switch (courseType/10%10){
                    case 0: //1 is a mark
                        break;
                    case 1: //1 and 1a
                        courseMarks.get(1).setName("1a");
                        courseMarks.add(new AviObject("1", new AviLocation(courseMarks.get(1).getAviLocation(), windDir +260, 0.02), ObjectTypes.Buoy, "orange", uuid));
                        break;
                    case 2:  //1 is a gate
                        addGate(courseMarks, new AviObject("Finish",courseMarks.get(1).getAviLocation(), ObjectTypes.Gate , "orange", uuid, 0.001, 90));
                        courseMarks.remove(1);
                        break;
                }

                break;
            case 4:  //triangular
                courseMarks.add(new AviObject("2", new AviLocation(courseMarks.get(1).getAviLocation(), windDir - 135, referencePoint, windDir - 45), ObjectTypes.Buoy, "orange", uuid));
                switch (courseType/10%10){
                    case 0: //1 is a mark
                        break;
                    case 1: //1 and 1a
                        courseMarks.get(1).setName("1a");
                        courseMarks.add(new AviObject("1", new AviLocation(courseMarks.get(1).getAviLocation(), windDir +260, 0.02), ObjectTypes.Buoy, "orange", uuid));
                        break;
                    case 2:  //1 is a gate
                        addGate(courseMarks, new AviObject("Finish",courseMarks.get(1).getAviLocation(), ObjectTypes.Gate , "orange", uuid, 0.001, 90));
                        courseMarks.remove(1);
                        break;
                }
                break;
            case 5:  //laser
                switch (courseType/10%10){ //from EurIlca recommendations
                    case 1: //course A - 3 large fleets
                        break;
                    case 2: //course B - 5 large fleets
                        break;
                    case 3: //course C - small fleets
                        break;
                    case 4: //course D - narrow waters
                        break;
                }
                break;

        }

    }

    private void addGate(List<AviObject> marksList , AviObject gateObject){
        marksList.add(new AviObject(gateObject.getName()+" S",new AviLocation(gateObject.getAviLocation(), windDir+gateObject.getGateDir() ,gateObject.getGateSpan()/2), gateObject.getEnumType() , gateObject.getColor() , uuid));
        marksList.add(new AviObject(gateObject.getName()+" P",new AviLocation(gateObject.getAviLocation(), windDir-gateObject.getGateDir() ,gateObject.getGateSpan()/2), gateObject.getEnumType() , gateObject.getColor() , uuid));
    }

    private RaceCourseDescriptor getTrapezoidDescriptor(AviLocation startlineLoc, int windDir, int startLineLength, int commonLength) {
        List<ObjectTypes> os = new ArrayList<>();
        os.add(ObjectTypes.StartFinishLine);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Buoy);
        os.add(ObjectTypes.Gate);
        os.add(ObjectTypes.FinishLine);
        List<DirDist> dds = new ArrayList<>();
        dds.add(new DirDist(0, 95));
        dds.add(new DirDist(0, 0.33f));
        dds.add(new DirDist(240, 0.1666f));
        dds.add(new DirDist(180, 0.33f));
        dds.add(new DirDist(120, 185));
        List<String> names = new ArrayList<>();
        names.add("StartLine");
        names.add("No4Gate");
        names.add("No1Mark");
        names.add("No2Mark");
        names.add("No3Gate");
        names.add("FinishLine");
        return new RaceCourseDescriptor(os, dds, names,startlineLoc,windDir,startLineLength,commonLength);
    }

    public int calculateTargetSpeed(BoatType bt){
        //TODO Implement
        return 0;
    }

    /**
     *
     * @param boatLength
     * @param numOfBoats
     * @param multiplyingFactor
     * @return The start line length
     */
    public int calculateStartLineLength(double boatLength, int numOfBoats, double multiplyingFactor){
        return (int)(boatLength*numOfBoats*multiplyingFactor);
    }

    public int getWindDir() {
        return windDir;
    }
    public void setWindDir(int windDir) {
        this.windDir = windDir;
    }
    public double getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
    public BoatType getBoatType() {
        return boatType;
    }
    public void setBoatType(BoatType boatType) {
        this.boatType = boatType;
    }
    public int getNumOfBoats() {
        return numOfBoats;
    }
    public void setNumOfBoats(int numOfBoats) {
        this.numOfBoats = numOfBoats;
    }
    public int getGoalTime() {
        return goalTime;
    }
    public void setGoalTime(int goalTime) {
        this.goalTime = goalTime;
    }
    public AviLocation getSignalBoatLoc() {
        return signalBoatLoc;
    }
    public void setSignalBoatLoc(AviLocation signalBoatLoc) {
        this.signalBoatLoc = signalBoatLoc;
    }
    public boolean isUpdated() {
        return isUpdated;
    }
    public Marks getMarks() {
        return marks;
    }
    public double getBoatLength() {
        return boatLength;
    }
    public void setBoatLength(double boatLength) {
        this.boatLength = boatLength;
    }
    public RaceCourseType getRaceCourseType() {
        return raceCourseType;
    }
    public void setRaceCourseType(RaceCourseType raceCourseType) {
        this.raceCourseType = raceCourseType;
    }
    public UUID getUuid() {
        return uuid;
    }
    public RaceCourseType getRaceCourseType(String rc){
        return raceCourseTypes.get(rc);
    }
    public double getBoatVMGUpwind() {
        return boatVMGUpwind;
    }
    public void setBoatVMGUpwind(double boatVMGUpwind) {
        this.boatVMGUpwind = boatVMGUpwind;
    }
    public double getBoatVMGReach() {
        return boatVMGReach;
    }
    public void setBoatVMGReach(double boatVMGReach) {
        this.boatVMGReach = boatVMGReach;
    }
    public double getBoatVMGRun() {
        return boatVMGRun;
    }
    public void setBoatVMGRun(double boatVMGRun) {
        this.boatVMGRun = boatVMGRun;
    }

}
