package prajwal.practice.crophelpproject.utils;


import androidx.annotation.NonNull;

import java.util.Arrays;

public class Disease {
    private String diseaseName;
    private String cropName;
    private String name;
    private String causes;
    private String cures;

    public Disease(String diseaseName, String cropName, String name, String causes, String cures) {
        this.diseaseName = diseaseName;
        this.cropName = cropName;
        this.name = name;
        this.causes = causes;
        this.cures = cures;
    }

    public Disease() {
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getCropName() {
        return cropName;
    }

    public String getName() {
        return name;
    }

    public String getCauses() {
        return causes;
    }

    public String getCures() {
        return cures;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "diseaseName=" + diseaseName+"\n"  +
                ", cropName=" + cropName +"\n" +
                ", name=" + name  + "\n"+
                ", causes=" + causes + "\n" +
                ", cures=" + cures ;
    }
}
