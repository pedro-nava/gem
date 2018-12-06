package com.gem.sistema.web.bean;

import static com.gem.sistema.util.UtilFront.generateNotificationFront;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

import com.gem.sistema.business.domain.Conctb;
import com.gem.sistema.business.domain.Eaid;

import com.gem.sistema.business.domain.TcPeriodo;
import com.gem.sistema.business.service.catalogos.EaidService;
import com.gem.sistema.business.service.catalogos.TcPeriodoService;
import com.gem.sistema.web.datamodel.DataModelGeneric;
import com.roonin.utils.UtilDate;

@ManagedBean(name = "eaidMB")
@ViewScoped
public class EaidMB extends AbstractMB {

	private static final Log LOG = LogFactory.getLog(EaidMB.class);

	private static final Boolean FALSE = Boolean.FALSE;
	private static final Boolean TRUE = Boolean.TRUE;

	private static final Integer TRIMESTRE = 3;
	/** The Constant VIEW_EDIT_ROW_ACTIVATE_PENCIL. */
	private static final String VIEW_EDIT_ROW_ACTIVATE_PENCIL = "jQuery('span.ui-icon-pencil').eq(";

	/** The Constant VIEW_EDIT_ROW_ACTIVATE_PENCIL_COMPLEMENT. */
	private static final String VIEW_EDIT_ROW_ACTIVATE_PENCIL_COMPLEMENT = ").each(function(){jQuery(this).click()});";

	/** The Constant FOCUS_BY_ROWID. */
	private static final String FOCUS_BY_ROWID = "PrimeFaces.focus('form1:object:%1$s:trim');";

	/** The Constant UPDATE_OBJETS. */
	private static final String UPDATE_OBJETS = "jQuery('#form1\\\\:hiddenUpdate').click();";

	private static final String CLICK_UPDATE = "jQuery('#form1\\\\\\\\:lasPage').click();";

	@ManagedProperty("#{tcPeriodoService}")
	private TcPeriodoService tcPeriodoService;

	@ManagedProperty("#{eaidService}")
	private EaidService eaidService;

	private Eaid eaid;
	private TcPeriodo tcPeriodo;
	private Conctb conctb;

	private String operador;
	private Integer oldValue;
	private Integer lastTrimestre;
	private Integer lastConse;
	private Integer trimestre;

	private Boolean bSalvar;
	private Boolean bModificar;
	private Boolean bCancelar;
	private Boolean bAdicionar;
	private Boolean bEdicion;

	private Integer idSector;
	private Integer indexCurrent;

	private Integer anio;

	private List<Eaid> listEadi;
	private List<TcPeriodo> listPeriodo;

	private DataModelGeneric<Eaid> dataModelEaid;

	@PostConstruct
	public void init() {
		LOG.info("INICIA EL PROCESO DE CAPTURA DE EAID");
		this.setIdSector(this.getUserDetails().getIdSector());
		conctb = this.eaidService.getAnioContable(idSector, 0l);
		eaid = new Eaid();

		this.listPeriodo = this.tcPeriodoService.findByPeriodo(TRIMESTRE);
		trimestre = listPeriodo.get(0).getPeriodo();

		this.iniciarlizarbandera();
		this.loadData();

	}

	public void loadData() {
		listEadi = this.eaidService.findByIdSector(this.getIdSector());
		dataModelEaid = new DataModelGeneric<Eaid>(listEadi);
		this.setbAdicionar(FALSE);
	}

	public void findByTrimestre() {
		listEadi = this.eaidService.findByTrimestre(trimestre, idSector);
		this.getDataModelEaid().setListT(listEadi);
	}

	public void addElement() {
		this.setbAdicionar(TRUE);
		Eaid eaid = new Eaid();

		listEadi.add(eaid);

		this.activateRowEdit(listEadi.size() - 1);
		RequestContext.getCurrentInstance().execute("PF('eaidWdg').paginator.setPage(" + (this.getPage() - 1) + ");");

		if (listEadi.size() > 20) {
			Integer indexOf = this.getRowCurrent(listEadi.size());
			this.activateRowEdit(indexOf);

			RequestContext.getCurrentInstance().execute(String.format(FOCUS_BY_ROWID, (listEadi.size() - 1)));
			RequestContext.getCurrentInstance().execute(CLICK_UPDATE);

		}

		dataModelEaid.setListT(listEadi);

	}

	public void delete(Integer index) {
		eaid = this.getDataModelEaid().getListT().get(index);
		if (null == eaid.getId() || eaid.getId() == 0) {
			listEadi.remove(eaid);

		} else {
			listEadi.remove(eaid);
			eaidService.delete(eaid);
		}
		generateNotificationFront(FacesMessage.SEVERITY_INFO, "Información!",
				"¡Se Eliminico Correctamente el Registro!");
	}

	public void iniciarlizarbandera() {
		this.bModificar = TRUE;
		this.bAdicionar = FALSE;
		this.bSalvar = FALSE;
		this.bCancelar = FALSE;
		this.bEdicion = FALSE;
	}

	/**
	 * Persiste la edicion de un registro.
	 *
	 * @param event the event
	 */
	public void onInitRowEdit(final RowEditEvent event) {
		eaid = (Eaid) event.getObject();
		if (null != event.getObject()) {

			if (null != eaid.getId() && eaid.getId() != 0) {
				this.setbAdicionar(TRUE);
				lastTrimestre = this.eaid.getTrimestre();
				lastConse = this.eaid.getConsecutivo();

				for (int i = 0; i < this.getDataModelEaid().getListT().size(); i++) {
					if (this.getDataModelEaid().getListT().get(i) == eaid) {
						indexCurrent = i;
						break;
					}

				}
			} else {
				this.setbAdicionar(FALSE);
			}
		}
	}

	/**
	 * On row edit.
	 *
	 * @param event the event
	 */
	public void onRowEdit(RowEditEvent event) {
		Eaid obj = (Eaid) event.getObject();

		Eaid existe = this.eaidService.findByIdSectorAndTrimestreAndCoonsecutivo(this.getIdSector(), obj.getTrimestre(),
				obj.getConsecutivo());
		Integer lastIndex = this.dataModelEaid.getListT().size() > 0 ? this.dataModelEaid.getListT().size() - 1 : 0;
		obj.setIdSector(this.getIdSector());
		obj.setIdRef(this.getUserDetails().getIdMunicipio());
		obj.setFechaCptura(UtilDate.getDateSystem());
		obj.setIdAnio(conctb.getId());
		obj.setCapturo(this.getUserDetails().getUsername());
		if (null == existe && !this.getbAdicionar()) {

			this.eaidService.save(obj);
			generateNotificationFront(FacesMessage.SEVERITY_INFO, "Información!",
					"Los Datos se Guardaron Correctamente");
			RequestContext.getCurrentInstance().execute(UPDATE_OBJETS);
			this.setbAdicionar(FALSE);

		} else if (this.getbAdicionar() && lastTrimestre == obj.getTrimestre() && lastConse == obj.getConsecutivo()) {
			this.eaidService.save(obj);
			this.setbAdicionar(FALSE);
			generateNotificationFront(FacesMessage.SEVERITY_INFO, "Información!",
					"Los Datos se Modificaron Correctamente");
			RequestContext.getCurrentInstance().execute(UPDATE_OBJETS);
		} else {
			generateNotificationFront(FacesMessage.SEVERITY_WARN, "Error!", "El trimestre : " + obj.getTrimestre()
					+ " Con Consecutivo: " + obj.getConsecutivo() + " \n¡Ya existen en la Base de Datos!");

			if (this.getbAdicionar() && (lastTrimestre != obj.getTrimestre() || lastConse != obj.getConsecutivo()))

				this.activateRowEdit(indexCurrent);
			else
				this.activateRowEdit(lastIndex);
		}

	}

	public void onRowCancel(RowEditEvent event) {

		generateNotificationFront(FacesMessage.SEVERITY_INFO, "Info!", "Edición Cancelada");
	}

	/**
	 * @param index
	 * @return
	 */
	public Integer getRowCurrent(Integer index) {
		Double rows = 20.0;
		Double size = index.doubleValue();
		Double row = (size % rows);
		Integer filas = row.intValue() - 1;
		if (filas < 0) {
			filas = 19;
		}

		return filas;
	}

	/**
	 * Activate row edit.
	 *
	 * @param index the index
	 */
	public void activateRowEdit(final int index) {
		LOG.info(":: Cerrar cuadro de dialogo ");
		final StringBuilder text = new StringBuilder();
		text.append(VIEW_EDIT_ROW_ACTIVATE_PENCIL);
		text.append(index);
		text.append(VIEW_EDIT_ROW_ACTIVATE_PENCIL_COMPLEMENT);
		text.append(" ");
		text.append(String.format(FOCUS_BY_ROWID, index));
		RequestContext.getCurrentInstance().execute(text.toString());
	}

	/**
	 * Activa el modo de edicion en una fila.
	 * 
	 * @param index fila a ser activada.
	 */
	/**
	 * @param index
	 */
	public void activateRowEditOnInsert(final int index) {
		LOG.info(":: Cerrar cuadro de dialogo ");
		final StringBuilder text = new StringBuilder();
		// text.append("PF('polizasWdg').filter();PF('polizasWdg').paginator.setPage(PF('polizasWdg').paginator.cfg.pageCount
		// - 1);");
		text.append(VIEW_EDIT_ROW_ACTIVATE_PENCIL);
		text.append(index);
		text.append(VIEW_EDIT_ROW_ACTIVATE_PENCIL_COMPLEMENT);
		text.append(" ");
		text.append(String.format(FOCUS_BY_ROWID, index));
		RequestContext.getCurrentInstance().execute(text.toString());
	}

	/**
	 * Gets the page.
	 *
	 * @return the page
	 */
	public int getPage() {
		int rows = dataModelEaid.getListT().size();
		int de = 1;
		double maxRow = 20;
		double pageActua = (rows * de) / maxRow;
		String page = "" + Math.ceil(pageActua);
		return Integer.parseInt(this.getValue(page)[0]);
	}

	/**
	 * Gets the value.
	 *
	 * @param data the data
	 * @return the value
	 */
	public String[] getValue(String data) {
		return data.split("\\.");
	}

	/**
	 * Go to last page.
	 */
	public void goToLastPage() {
		Integer size = this.dataModelEaid.getListT().size();
		if (this.getbAdicionar() == Boolean.TRUE)
			if (size >= 20) {
				this.activateRowEditOnInsert(size);

			} else {

				this.activateRowEdit(size - 1);

			}
	}

	public void activeRowPostPage() {
		this.activateRowEdit(this.getDataModelEaid().getListT().size() - 1);
	}

	public EaidService getEaidService() {
		return eaidService;
	}

	public void setEaidService(EaidService eaidService) {
		this.eaidService = eaidService;
	}

	public void save() {

	}

	public Eaid getEaid() {
		return eaid;
	}

	public void setEaid(Eaid eaid) {
		this.eaid = eaid;
	}

	public List<Eaid> getListEadi() {
		return listEadi;
	}

	public void setListEadi(List<Eaid> listEadi) {
		this.listEadi = listEadi;
	}

	public DataModelGeneric<Eaid> getDataModelEaid() {
		return dataModelEaid;
	}

	public void setDataModelEaid(DataModelGeneric<Eaid> dataModelEaid) {
		this.dataModelEaid = dataModelEaid;
	}

	public TcPeriodoService getTcPeriodoService() {
		return tcPeriodoService;
	}

	public void setTcPeriodoService(TcPeriodoService tcPeriodoService) {
		this.tcPeriodoService = tcPeriodoService;
	}

	public TcPeriodo getTcPeriodo() {
		return tcPeriodo;
	}

	public void setTcPeriodo(TcPeriodo tcPeriodo) {
		this.tcPeriodo = tcPeriodo;
	}

	public List<TcPeriodo> getListPeriodo() {
		return listPeriodo;
	}

	public void setListPeriodo(List<TcPeriodo> listPeriodo) {
		this.listPeriodo = listPeriodo;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}

	public Integer getOldValue() {
		return oldValue;
	}

	public void setOldValue(Integer oldValue) {
		this.oldValue = oldValue;
	}

	public Boolean getbSalvar() {
		return bSalvar;
	}

	public void setbSalvar(Boolean bSalvar) {
		this.bSalvar = bSalvar;
	}

	public Boolean getbModificar() {
		return bModificar;
	}

	public void setbModificar(Boolean bModificar) {
		this.bModificar = bModificar;
	}

	public Boolean getbCancelar() {
		return bCancelar;
	}

	public void setbCancelar(Boolean bCancelar) {
		this.bCancelar = bCancelar;
	}

	public Boolean getbAdicionar() {
		return bAdicionar;
	}

	public void setbAdicionar(Boolean bAdicionar) {
		this.bAdicionar = bAdicionar;
	}

	public Boolean getbEdicion() {
		return bEdicion;
	}

	public void setbEdicion(Boolean bEdicion) {
		this.bEdicion = bEdicion;
	}

	public Integer getIdSector() {
		return idSector;
	}

	public void setIdSector(Integer idSector) {
		this.idSector = idSector;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public Conctb getConctb() {
		return conctb;
	}

	public void setConctb(Conctb conctb) {
		this.conctb = conctb;
	}

	public Integer getTrimestre() {
		return trimestre;
	}

	public void setTrimestre(Integer trimestre) {
		this.trimestre = trimestre;
	}

}
