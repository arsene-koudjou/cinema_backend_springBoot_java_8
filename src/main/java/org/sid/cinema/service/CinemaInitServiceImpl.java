package org.sid.cinema.service;

import javafx.scene.input.DataFormat;
import org.sid.cinema.dao.*;
import org.sid.cinema.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Service
@Transactional
public class CinemaInitServiceImpl implements  ICinemaInitService {
    @Autowired
    private VilleRepository villeRepository;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private ProjectionRepository projectionRepository;

    @Override
    public void initVilles() {
        Stream.of("Casablanca","Douala","Paris","Rome").forEach(nameVille->{
            Ville ville = new Ville();
            ville.setName(nameVille);
            villeRepository.save(ville);
        });
    }

    @Override
    public void initCinemas() {
        villeRepository.findAll().forEach(ville -> {
            Stream.of("IMAX","Douala Bercy","Olympiade","Bella Roma").forEach(nameCinema->{
                Cinema cinema  = new Cinema();
                cinema.setName(nameCinema);
                cinema.setNombreSalles(3+(int)(Math.random()*7));
                cinema.setVille(ville);
                cinemaRepository.save(cinema);
            });
        });
    }

    @Override
    public void initSalles() {
        cinemaRepository.findAll().forEach(cinema -> {
           for (int i=0; i<cinema.getNombreSalles();i++){
               Salle salle = new Salle();
               salle.setName("salle"+(i+1));
               salle.setCinema(cinema);
               salle.setNombrePlace(15+(int)(Math.random()*20));
               salleRepository.save(salle);
           }
        });
    }

    @Override
    public void initPlaces() {
      salleRepository.findAll().forEach(salle -> {
          for (int i=0; i < salle.getNombrePlace(); i++){
              Place place = new Place();
              place.setNumero(i+1);
              place.setSalle(salle);
              placeRepository.save(place);
          }
      });
    }

    @Override
    public void initSeances() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    Stream.of("12:00","15:00","17:00","19:00","21:00").forEach(s -> {
        Seance seance = new Seance();
        try {
            seance.setHeureDebut(dateFormat.parse(s));
            seanceRepository.save(seance);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    });
    }

    @Override
    public void initCategories() {

        Stream.of("Action","Fiction","Drame").forEach(cat -> {
            Categorie categorie = new Categorie();
            categorie.setName(cat);
            categorieRepository.save(categorie);
        });
    }

    @Override
    public void initFilms() {
        double[] durees = new  double[] {1,1.5,2,2.5,3};
        List<Categorie> categories = categorieRepository.findAll();
    Stream.of("hommes en colere","forrest gump","green book","la ligne verte","le parrain","le seigneur des anneaux").forEach(f-> {
        Film film = new Film();
        film.setTitre(f);
        film.setDuree(durees[new Random().nextInt(durees.length)]);
        film.setPhoto(f.replaceAll(" ","")+".jpg");
        film.setCategorie(categories.get(new  Random().nextInt(categories.size())));
        filmRepository.save(film);
    });
    }

    @Override
    public void initProjections() {
        double[] prices = new  double[] {30,60,80,90,100};
        List<Film> films = filmRepository.findAll();
     villeRepository.findAll().forEach(ville -> {
         ville.getCinemas().forEach(cinema -> {
             cinema.getSalles().forEach(salle -> {
                 int index = new  Random().nextInt(films.size());
                  Film film = films.get(index);
                     seanceRepository.findAll().forEach(seance -> {
                         Projection projection = new Projection();
                         projection.setDateProjection(new Date());
                         projection.setFilm(film);
                         projection.setPrix(prices[new  Random().nextInt(prices.length)]);
                         projection.setSalle(salle);
                         projection.setSeance(seance);
                         projectionRepository.save(projection);
                     });

             });
         });
     });
    }

    @Override
    public void initTickets() {
     projectionRepository.findAll().forEach(projection -> {
         projection.getSalle().getPlaces().forEach(place -> {
             Ticket ticket = new Ticket();
             ticket.setPlace(place);
             ticket.setPrix(projection.getPrix());
             ticket.setProjection(projection);
             ticket.setReserve(false);
             ticketRepository.save(ticket);
         });
     });
    }
}
