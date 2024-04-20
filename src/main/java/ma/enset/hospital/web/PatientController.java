package ma.enset.hospital.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ma.enset.hospital.entities.Patient;
import ma.enset.hospital.repositories.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    private PatientRepository patientRepository;
@GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(name = "page",defaultValue = "0") int page,
                        @RequestParam(name = "size",defaultValue = "4") int size ,
                        @RequestParam(name = "keyword",defaultValue = "") String kw
){
    Page<Patient> pagePatient = patientRepository.findByNomContains(kw,PageRequest.of(page, size));
         model.addAttribute("pages", new int[pagePatient.getTotalPages()]);
        model.addAttribute("listPatients", pagePatient.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", kw);
    return "patients";
}
@GetMapping("/admin/delete")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public String delete(long id ,String keyword , int page){
 patientRepository.deleteById(id);
  return "redirect:/user/index?page="+page+"&keyword="+keyword;
 }
    @GetMapping("/")
    public String home(){
        return "redirect:/user/index";
    }
    @GetMapping("/admin/formPatients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String formPatient(Model model){
    model.addAttribute("patient", new Patient());
    return "formPatient";
    }
    @PostMapping(path="/admin/save")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String save(Model model, @Valid Patient patient, BindingResult bindingResult ,
                     @RequestParam(defaultValue = "") String keyword ,
                       @RequestParam(defaultValue = "0")  int page){
       if(bindingResult.hasErrors()) return "formPatient";
        patientRepository.save(patient);
        return "redirect:/user/index?page="+page+"&keyword="+keyword;

    }
    @GetMapping("/admin/editPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editPatient(Model model,Long id ,String keyword , int page){
    Patient patient = patientRepository.findById(id).orElse(null);
    if(patient == null) throw new RuntimeException("Patient not found");
        model.addAttribute("patient",patient);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);
        return "editPatient";
    }
}
