package df.farmponds;

import java.util.List;

import df.farmponds.Models.PondImage;

public class Class_addpondResponse
{

    private String TempPond_ID;
    private String Pond_ID;
    private List<PondImage> PondImage;


    public String getTempPond_ID() {
        return TempPond_ID;
    }

    public void setTempPond_ID(String tempPond_ID) {
        TempPond_ID = tempPond_ID;
    }

    public String getPond_ID() {
        return Pond_ID;
    }

    public void setPond_ID(String pond_ID) {
        Pond_ID = pond_ID;
    }

    public List<df.farmponds.Models.PondImage> getPondImage() {
        return PondImage;
    }

    public void setPondImage(List<df.farmponds.Models.PondImage> pondImage) {
        PondImage = pondImage;
    }
}
