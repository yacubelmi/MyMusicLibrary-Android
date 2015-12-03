package uk.bcu.model;

public class MusicData {

    //private variables
    int _id;
    String _name;
    String _artist;

    // Empty constructor
    public MusicData() {

    }

    // constructor
    public MusicData(int id, String name, String artist) {
        this._id = id;
        this._name = name;
        this._artist = artist;
    }

    // constructor
    public MusicData(String name, String artist) {
        this._name = name;
        this._artist = artist;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }

    // getting artist
    public String getArtist() {
        return this._artist;
    }

    // setting artist
    public void setArtist(String artist) {
        this._artist = artist;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UserInfo [name=" + _name + ", artist=" + _artist + "]";
    }

}
