//package org.worshipsongs.service;
//
//import org.worshipsongs.domain.ServiceSong;
//import org.worshipsongs.domain.Song;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * Author : Madasamy
// * Version : 3.x
// */
//
//public interface ISongService
//{
//
//    void copyDatabase(String databasePath, boolean dropDatabase) throws IOException;
//
//    void open();
//
//    List<Song> findAll();
//
//    List<Song> filterSongs(String type, String query, List<Song> songs);
//
//   // List<Song> filterSongs(String text, List<Song> songs);
//
//    List<ServiceSong> filteredServiceSongs(String query, List<ServiceSong> serviceSongs);
//
//    List<Song> findByAuthorId(int id);
//
//    List<Song> findByTopicId(int id);
//
//    List<Song> findBySongBookId(int id);
//
//    Song findContentsByTitle(String title);
//
//    String getTitle(boolean tamilLanguage, ServiceSong serviceSong);
//}
