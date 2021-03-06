package com.gem.sistema.web.bean;

import static com.gem.sistema.util.UtilFront.generateNotificationFront;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PageEvent;

import com.gem.sistema.business.domain.TcPeriodo;
import com.gem.sistema.business.dto.Pm5911DTO;
import com.gem.sistema.business.repository.catalogs.TcPeriodoRepositoy;
import com.gem.sistema.business.repository.catalogs.TrEtqTablasRepository;
import com.gem.sistema.business.service.catalogos.Pm5911Service;

@ManagedBean(name = "pm5911MB")
@ViewScoped
public class Pm5911MB extends AbstractMB {

	private static final String TABLE_NAME = "pm5911";
	private static final Integer SEMESTRE = 6;

	@ManagedProperty("#{pm5911Service}")
	private Pm5911Service pm5911Service;

	@ManagedProperty("#{tcPeriodoRepositoy}")
	private TcPeriodoRepositoy tcPeriodoRepositoy;

	@ManagedProperty("#{trEtqTablasRepository}")
	private TrEtqTablasRepository trEtqTablasRepository;

	private Pm5911DTO pm5911dto;

	private List<Pm5911DTO> listPmDTO;

	private Integer semestre;

	private List<TcPeriodo> listSemestre;

	private boolean bEditar = false;
	private boolean bSalva = false;
	private boolean bAdicionar = false;
	private boolean bUpdate = false;
	private boolean bBorrar = false;
	private boolean bModificar = false;
	private boolean bCancelar = false;
	private boolean bMostrarAdd = true;
	private boolean bLimpiar = false;

	private int currentPage = 0;

	@PostConstruct
	public void init() {
		this.findAll();

		listSemestre = tcPeriodoRepositoy.findByTipoPeriodo(SEMESTRE);
		pm5911dto = new Pm5911DTO();
		pm5911dto.setCapturo(this.getUserDetails().getUsername());
		pm5911dto.setIdSector(this.getUserDetails().getIdSector());
		semestre = listSemestre.get(0).getPeriodo();
		if (CollectionUtils.isEmpty(listPmDTO))
			createNewElement(0);

	}

	public void reset() {
		this.findAll();
		bAdicionar = false;
		bModificar = true;
		bMostrarAdd = true;
		bUpdate = true;

	}

	public void save() {

		pm5911dto = listPmDTO.get(currentPage);
		Integer existe = this.trEtqTablasRepository.validaSemestre(pm5911dto.getSemestre().toString(), TABLE_NAME);
		pm5911dto.setIdAnio(0);

		pm5911dto.setIdSector(this.getUserDetails().getIdSector());
		pm5911dto.setCapturo(this.getUserDetails().getUsername());
		bAdicionar = true;
		if (existe == 0 && !bUpdate) {

			bAdicionar = false;

			listPmDTO = this.pm5911Service.save(pm5911dto);
			bBorrar = true;
			bCancelar = false;
			bModificar = true;
			bMostrarAdd = true;
			bLimpiar = false;
			generateNotificationFront(FacesMessage.SEVERITY_INFO, "Info!", "Se ha guardado con exito el registro");

		} else if (bUpdate) {
			pm5911dto = listPmDTO.get(currentPage);
			listPmDTO = this.pm5911Service.modify(pm5911dto);
			bAdicionar = false;
			bUpdate = false;
			bCancelar = false;
			bModificar = true;
			bMostrarAdd = true;
			bBorrar = true;
			bLimpiar = false;
			generateNotificationFront(FacesMessage.SEVERITY_INFO, "Info!", "!Se ha modificado con exito el registro!");
		} else {
			listPmDTO.set(currentPage, pm5911dto);
			generateNotificationFront(FacesMessage.SEVERITY_WARN, "Error!", "!El semestre ya existe!");
			bAdicionar = true;
			bCancelar = true;
		}

	}

	public void findAll() {
		bBorrar = false;
		bModificar = false;
		bCancelar = true;
		bLimpiar = true;
		listPmDTO = (List<Pm5911DTO>) this.pm5911Service.findByTableName(TABLE_NAME);
		if (CollectionUtils.isNotEmpty(listPmDTO)) {
			if (StringUtils.isNotBlank(listPmDTO.get(0).getFechaIng())) {
				bBorrar = true;
				bModificar = true;
			}
		} else {
			listPmDTO.add(new Pm5911DTO());
		}

		if (listPmDTO.size() > 1) {
			bAdicionar = false;
			bBorrar = true;
		}
	}

	public void cambiarPagina(PageEvent event) {
		currentPage = event.getPage();

	}

	public void addElement() {
		bAdicionar = true;
		bCancelar = true;
		bBorrar = false;
		bLimpiar = true;
		listPmDTO.add(new Pm5911DTO());
		if (StringUtils.isEmpty(listPmDTO.get(0).getFechaIng())) {
			for (int i = 0; i < listPmDTO.size(); i++) {
				listPmDTO.remove(i);
			}

		}

		if (listPmDTO.size() == 2) {
			RequestContext.getCurrentInstance()
					.execute("PF('dataGrid').paginator.setPage(" + (listPmDTO.size() - 1) + ");");
			listPmDTO.set(1, new Pm5911DTO());

		} else {
			listPmDTO.add(0, pm5911dto);
		}

	}

	public void createNewElement(Integer index) {

		listPmDTO.add(index, pm5911dto);
	}

	public void borrar() {
		pm5911dto = listPmDTO.get(currentPage);
		pm5911Service.delete(pm5911dto);
		this.findAll();
		generateNotificationFront(FacesMessage.SEVERITY_INFO, "Info!", "!Se borro con exito el registro!");

	}

	public void modificar() {
		bUpdate = true;
		bAdicionar = true;
		bMostrarAdd = false;
		bLimpiar = false;
		bBorrar = false;
		bCancelar = false;
	}

	public Pm5911Service getPm5911Service() {
		return pm5911Service;
	}

	public void setPm5911Service(Pm5911Service pm5911Service) {
		this.pm5911Service = pm5911Service;
	}

	public TcPeriodoRepositoy getTcPeriodoRepositoy() {
		return tcPeriodoRepositoy;
	}

	public void setTcPeriodoRepositoy(TcPeriodoRepositoy tcPeriodoRepositoy) {
		this.tcPeriodoRepositoy = tcPeriodoRepositoy;
	}

	public Pm5911DTO getPm5911dto() {
		return pm5911dto;
	}

	public void setPm5911dto(Pm5911DTO pm5911dto) {
		this.pm5911dto = pm5911dto;
	}

	public List<Pm5911DTO> getListPmDTO() {
		return listPmDTO;
	}

	public void setListPmDTO(List<Pm5911DTO> listPmDTO) {
		this.listPmDTO = listPmDTO;
	}

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}

	public List<TcPeriodo> getListSemestre() {
		return listSemestre;
	}

	public void setListSemestre(List<TcPeriodo> listSemestre) {
		this.listSemestre = listSemestre;
	}

	public boolean isbEditar() {
		return bEditar;
	}

	public void setbEditar(boolean bEditar) {
		this.bEditar = bEditar;
	}

	public boolean isbSalva() {
		return bSalva;
	}

	public void setbSalva(boolean bSalva) {
		this.bSalva = bSalva;
	}

	public boolean isbAdicionar() {
		return bAdicionar;
	}

	public void setbAdicionar(boolean bAdicionar) {
		this.bAdicionar = bAdicionar;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public TrEtqTablasRepository getTrEtqTablasRepository() {
		return trEtqTablasRepository;
	}

	public void setTrEtqTablasRepository(TrEtqTablasRepository trEtqTablasRepository) {
		this.trEtqTablasRepository = trEtqTablasRepository;
	}

	public boolean isbUpdate() {
		return bUpdate;
	}

	public void setbUpdate(boolean bUpdate) {
		this.bUpdate = bUpdate;
	}

	public boolean isbBorrar() {
		return bBorrar;
	}

	public void setbBorrar(boolean bBorrar) {
		this.bBorrar = bBorrar;
	}

	public boolean isbModificar() {
		return bModificar;
	}

	public void setbModificar(boolean bModificar) {
		this.bModificar = bModificar;
	}

	public boolean isbCancelar() {
		return bCancelar;
	}

	public void setbCancelar(boolean bCancelar) {
		this.bCancelar = bCancelar;
	}

	public boolean isbMostrarAdd() {
		return bMostrarAdd;
	}

	public void setbMostrarAdd(boolean bMostrarAdd) {
		this.bMostrarAdd = bMostrarAdd;
	}

	public boolean isbLimpiar() {
		return bLimpiar;
	}

	public void setbLimpiar(boolean bLimpiar) {
		this.bLimpiar = bLimpiar;
	}

}
