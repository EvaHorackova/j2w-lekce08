package cz.czechitas.java2webapps.lekce8.controller;

import cz.czechitas.java2webapps.lekce8.entity.Osoba;
import cz.czechitas.java2webapps.lekce8.repository.OsobaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class OsobaController {

  @Autowired //  označuje konstruktor, který Spring použije pro vytvoření objektu a dosazení požadovaných parametrů
  private final OsobaRepository osobaRepository;

  public OsobaController(OsobaRepository osobaRepository) { // dědí z crud
    this.osobaRepository = osobaRepository;
  }

  @InitBinder
  public void nullStringBinding(WebDataBinder binder) {
    //prázdné textové řetězce nahradit null hodnotou
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

  @GetMapping("/")
  public ModelAndView seznam() {
    // načíst seznam osob
    return new ModelAndView("seznam")
            .addObject("osoby", osobaRepository.findAll());
  }

  @GetMapping("/novy")
  public ModelAndView novy() {
    return new ModelAndView("detail")
            .addObject("osoba", new Osoba());
  }

  @PostMapping("/novy")
  public String pridat(@ModelAttribute("osoba") @Valid Osoba osoba, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "detail";
    }
    // uložit údaj o nové osobě
    osoba.setId(null); // pojistka při přidání nové osoby
    osobaRepository.save(osoba);
    return "redirect:/";
  }

  @GetMapping("/{id:[0-9]+}") // číslice 0 až 9 a může tam být 1 až N takových znaků
  public ModelAndView detail(@PathVariable long id) {
    // načíst údaj o osobě
    Optional<Osoba> osoba = osobaRepository.findById(id); // metoda vrací optional
    if (osoba.isEmpty()) { // pokud se ID v DB nenašlo -> 404 not found
      throw new ResponseStatusException(HttpStatus.NOT_FOUND); // 404 not found
    }
    return new ModelAndView("detail")
            .addObject("osoba", osoba.get());
  }

  @PostMapping("/{id:[0-9]+}")
  public String ulozit(@ModelAttribute("osoba") @Valid Osoba osoba, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "detail";
    }
    // uložit údaj o osobě
    osobaRepository.save(osoba); // save uloží nový záznam nebo aktualizuje stávající
    return "redirect:/";
  }

  @PostMapping(value = "/{id:[0-9]+}", params = "akce=smazat")
  public String smazat(@PathVariable long id) {
    // smazat údaj o osobě
    osobaRepository.deleteById(id);
    return "redirect:/";
  }

}