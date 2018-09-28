package io.github.johnjcool.dvblink.live.tv.remote.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class VideoInfo implements Serializable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("start_time")
    private long startTime;
    @JsonProperty("duration")
    private int duration;

    @JsonProperty("short_desc")
    private String shortDesc;
    @JsonProperty("subname")
    private String subname;
    @JsonProperty("language")
    private String language;
    @JsonProperty("actors")
    private String actors;
    @JsonProperty("directors")
    private String directors;
    @JsonProperty("writers")
    private String writers;
    @JsonProperty("producers")
    private String producers;
    @JsonProperty("guests")
    private String guests;
    @JsonProperty("categories")
    private String categories;
    @JsonProperty("image")
    private String image;

    @JsonProperty("year")
    private int year;
    @JsonProperty("epcatode_num")
    private int epcatodeNum;
    @JsonProperty("season_num")
    private int seasonNum;
    @JsonProperty("stars_num")
    private int starsNum;
    @JsonProperty("starsmax_num")
    private int starsMaxNum;

    @JsonProperty("hdtv")
    private boolean hdtv;
    @JsonProperty("premiere")
    private boolean premiere;
    @JsonProperty("repeat")
    private boolean repeat;
    @JsonProperty("cat_action")
    private boolean catAction;
    @JsonProperty("cat_adult")
    private boolean catAdult;
    @JsonProperty("cat_comedy")
    private boolean catComedy;
    @JsonProperty("cat_documentary")
    private boolean catDocumentary;
    @JsonProperty("cat_drama")
    private boolean catDrama;
    @JsonProperty("cat_educational")
    private boolean catEducational;
    @JsonProperty("cat_horror")
    private boolean catHorror;
    @JsonProperty("cat_kids")
    private boolean catKids;
    @JsonProperty("cat_movie")
    private boolean catMovie;
    @JsonProperty("cat_music")
    private boolean catMusic;
    @JsonProperty("cat_news")
    private boolean catNews;
    @JsonProperty("cat_reality")
    private boolean catReality;
    @JsonProperty("cat_romance")
    private boolean catRomance;
    @JsonProperty("cat_scifi")
    private boolean catScifi;
    @JsonProperty("cat_serial")
    private boolean catSerial;
    @JsonProperty("cat_soap")
    private boolean catSoap;
    @JsonProperty("cat_special")
    private boolean catSpecial;
    @JsonProperty("cat_sports")
    private boolean catSports;
    @JsonProperty("cat_thriller")
    private boolean catThriller;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getWriters() {
        return writers;
    }

    public void setWriters(String writers) {
        this.writers = writers;
    }

    public String getProducers() {
        return producers;
    }

    public void setProducers(String producers) {
        this.producers = producers;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getEpcatodeNum() {
        return epcatodeNum;
    }

    public void setEpcatodeNum(int epcatodeNum) {
        this.epcatodeNum = epcatodeNum;
    }

    public int getSeasonNum() {
        return seasonNum;
    }

    public void setSeasonNum(int seasonNum) {
        this.seasonNum = seasonNum;
    }

    public int getStarsNum() {
        return starsNum;
    }

    public void setStarsNum(int starsNum) {
        this.starsNum = starsNum;
    }

    public int getStarsMaxNum() {
        return starsMaxNum;
    }

    public void setStarsMaxNum(int starsMaxNum) {
        this.starsMaxNum = starsMaxNum;
    }

    public boolean isHdtv() {
        return hdtv;
    }

    public void setHdtv(boolean hdtv) {
        this.hdtv = hdtv;
    }

    public boolean isPremiere() {
        return premiere;
    }

    public void setPremiere(boolean premiere) {
        this.premiere = premiere;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isCatAction() {
        return catAction;
    }

    public void setCatAction(boolean catAction) {
        this.catAction = catAction;
    }

    public boolean isCatAdult() {
        return catAdult;
    }

    public void setCatAdult(boolean catAdult) {
        this.catAdult = catAdult;
    }

    public boolean isCatComedy() {
        return catComedy;
    }

    public void setCatComedy(boolean catComedy) {
        this.catComedy = catComedy;
    }

    public boolean isCatDocumentary() {
        return catDocumentary;
    }

    public void setCatDocumentary(boolean catDocumentary) {
        this.catDocumentary = catDocumentary;
    }

    public boolean isCatDrama() {
        return catDrama;
    }

    public void setCatDrama(boolean catDrama) {
        this.catDrama = catDrama;
    }

    public boolean isCatEducational() {
        return catEducational;
    }

    public void setCatEducational(boolean catEducational) {
        this.catEducational = catEducational;
    }

    public boolean isCatHorror() {
        return catHorror;
    }

    public void setCatHorror(boolean catHorror) {
        this.catHorror = catHorror;
    }

    public boolean isCatKids() {
        return catKids;
    }

    public void setCatKids(boolean catKids) {
        this.catKids = catKids;
    }

    public boolean isCatMovie() {
        return catMovie;
    }

    public void setCatMovie(boolean catMovie) {
        this.catMovie = catMovie;
    }

    public boolean isCatMusic() {
        return catMusic;
    }

    public void setCatMusic(boolean catMusic) {
        this.catMusic = catMusic;
    }

    public boolean isCatNews() {
        return catNews;
    }

    public void setCatNews(boolean catNews) {
        this.catNews = catNews;
    }

    public boolean isCatReality() {
        return catReality;
    }

    public void setCatReality(boolean catReality) {
        this.catReality = catReality;
    }

    public boolean isCatRomance() {
        return catRomance;
    }

    public void setCatRomance(boolean catRomance) {
        this.catRomance = catRomance;
    }

    public boolean isCatScifi() {
        return catScifi;
    }

    public void setCatScifi(boolean catScifi) {
        this.catScifi = catScifi;
    }

    public boolean isCatSerial() {
        return catSerial;
    }

    public void setCatSerial(boolean catSerial) {
        this.catSerial = catSerial;
    }

    public boolean isCatSoap() {
        return catSoap;
    }

    public void setCatSoap(boolean catSoap) {
        this.catSoap = catSoap;
    }

    public boolean isCatSpecial() {
        return catSpecial;
    }

    public void setCatSpecial(boolean catSpecial) {
        this.catSpecial = catSpecial;
    }

    public boolean isCatSports() {
        return catSports;
    }

    public void setCatSports(boolean catSports) {
        this.catSports = catSports;
    }

    public boolean isCatThriller() {
        return catThriller;
    }

    public void setCatThriller(boolean catThriller) {
        this.catThriller = catThriller;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "name='" + name + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", shortDesc='" + shortDesc + '\'' +
                ", subname='" + subname + '\'' +
                ", language='" + language + '\'' +
                ", actors='" + actors + '\'' +
                ", directors='" + directors + '\'' +
                ", writers='" + writers + '\'' +
                ", producers='" + producers + '\'' +
                ", guests='" + guests + '\'' +
                ", categories='" + categories + '\'' +
                ", image='" + image + '\'' +
                ", year=" + year +
                ", epcatodeNum=" + epcatodeNum +
                ", seasonNum=" + seasonNum +
                ", starsNum=" + starsNum +
                ", starsMaxNum=" + starsMaxNum +
                ", hdtv=" + hdtv +
                ", premiere=" + premiere +
                ", repeat=" + repeat +
                ", catAction=" + catAction +
                ", catAdult=" + catAdult +
                ", catComedy=" + catComedy +
                ", catDocumentary=" + catDocumentary +
                ", catDrama=" + catDrama +
                ", catEducational=" + catEducational +
                ", catHorror=" + catHorror +
                ", catKids=" + catKids +
                ", catMovie=" + catMovie +
                ", catMusic=" + catMusic +
                ", catNews=" + catNews +
                ", catReality=" + catReality +
                ", catRomance=" + catRomance +
                ", catScifi=" + catScifi +
                ", catSerial=" + catSerial +
                ", catSoap=" + catSoap +
                ", catSpecial=" + catSpecial +
                ", catSports=" + catSports +
                ", catThriller=" + catThriller +
                '}';
    }
}
