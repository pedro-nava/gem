package com.gem.sistema.web.bean;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import com.gem.sistema.business.domain.Firmas;
import com.gem.sistema.business.repository.catalogs.ConctbRepository;
import com.gem.sistema.business.repository.catalogs.FirmasRepository;
import com.gem.sistema.business.service.catalogos.ValidatePolicyService;

// TODO: Auto-generated Javadoc
/**
 * The Class Conciliacion5000vs8000DetalleMB.
 */
@ManagedBean
@ViewScoped
public class Conciliacion5000vs8000DetalleMB extends BaseDirectReport {

	/** The firmas repository. */
	@ManagedProperty("#{firmasRepository}")
	private FirmasRepository firmasRepository;

	@ManagedProperty("#{validatePolicyService}")
	private ValidatePolicyService validatePolicyService;

	@ManagedProperty("#{conctbRepository}")
	private ConctbRepository conctbRepository;

	/** The mes inicial. */
	private Integer mesInicial;

	/** The mes final. */
	private Integer mesFinal;

	/** The partida. */
	private Integer partida;

	/** The cuentas con saldo cero. */
	private boolean cuentasConSaldoCero;

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {

		LOGGER.info(":: En postconsruct Conciliacion5000vs8000DetalleMB ");
		jasperReporteName = "Conciliacion5000vs8000Detalle";
		endFilename = jasperReporteName + ".pdf";
		cuentasConSaldoCero = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gem.sistema.web.bean.BaseDirectReport#getParametersReports()
	 */
	public Map<String, Object> getParametersReports() {
		Firmas firmas = firmasRepository.findAllByIdsector(getUserDetails().getIdSector());
		Map<String, Object> parameters = new java.util.HashMap<String, Object>();

		parameters.put("P_SECTOR", getUserDetails().getIdSector());
		parameters.put("P_IMG", this.getUserDetails().getPathImgCab1());
		parameters.put("P_CAMPO1", firmas.getCampo1());
		parameters.put("P_L1", firmas.getL1());
		parameters.put("P_N1", firmas.getN1());
		parameters.put("P_L2", firmas.getL2());
		parameters.put("P_N2", firmas.getN2());
		parameters.put("P_L3", firmas.getL3());
		parameters.put("P_N3", firmas.getN3());
		parameters.put("P_L4", firmas.getL4());
		parameters.put("P_N4", firmas.getN4());
		parameters.put("P_MES_INICIAL", mesInicial);
		parameters.put("P_MES_FINAL", mesFinal);
		parameters.put("P_SSSCTA", String.valueOf(partida));
		parameters.put("P_CUENTAS_CON_SALDO_CERO", cuentasConSaldoCero ? "S" : "N");

		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gem.sistema.web.bean.BaseDirectReport#generaReporteSimple(int)
	 */
	public StreamedContent generaReporteSimple(int type) {
		return null;
	}

	/**
	 * Creates the file pdf inline validate.
	 */
	public void createFilePdfInlineValidate() {
		if (validInputs()) {
			Integer monthValidate = mesFinal;
			Integer monthOpen = this.conctbRepository.findByIdsector(this.getUserDetails().getIdSector()).getMesemp();
			if (monthOpen < mesInicial) {
				monthValidate = mesInicial;
			} else if (monthOpen < mesFinal) {
				monthValidate = mesFinal;
			}
			if (this.validatePolicyService.isOpenMonth(monthValidate, null,
					this.getUserDetails().getIdSector()) == Boolean.TRUE) {
				createFilePdfInline();
				RequestContext.getCurrentInstance()
						.execute("$(PrimeFaces.escapeClientId('form1:panelReporte')).css('height', '45em');");
			}
			RequestContext.getCurrentInstance().execute("PF('blockUIWidget').unblock();");
		}
	}

	/**
	 * Valid inputs.
	 *
	 * @return true, if successful
	 */
	private boolean validInputs() {
		boolean flag = true;

		if (mesFinal < mesInicial) {
			flag = false;
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, StringUtils.EMPTY,
					"Mes inicial debe ser menor o igual al mes final.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		if (null == partida) {
			flag = false;
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, StringUtils.EMPTY,
					"Favor de capturar una Partida.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		RequestContext.getCurrentInstance().execute("PF('blockUIWidget').unblock();");
		return flag;
	}

	/**
	 * Gets the firmas repository.
	 *
	 * @return the firmas repository
	 */
	public FirmasRepository getFirmasRepository() {
		return firmasRepository;
	}

	/**
	 * Sets the firmas repository.
	 *
	 * @param firmasRepository
	 *            the new firmas repository
	 */
	public void setFirmasRepository(FirmasRepository firmasRepository) {
		this.firmasRepository = firmasRepository;
	}

	/**
	 * Gets the mes inicial.
	 *
	 * @return the mes inicial
	 */
	public Integer getMesInicial() {
		return mesInicial;
	}

	/**
	 * Sets the mes inicial.
	 *
	 * @param mesInicial
	 *            the new mes inicial
	 */
	public void setMesInicial(Integer mesInicial) {
		this.mesInicial = mesInicial;
	}

	/**
	 * Gets the mes final.
	 *
	 * @return the mes final
	 */
	public Integer getMesFinal() {
		return mesFinal;
	}

	/**
	 * Sets the mes final.
	 *
	 * @param mesFinal
	 *            the new mes final
	 */
	public void setMesFinal(Integer mesFinal) {
		this.mesFinal = mesFinal;
	}

	/**
	 * Gets the partida.
	 *
	 * @return the partida
	 */
	public Integer getPartida() {
		return partida;
	}

	/**
	 * Sets the partida.
	 *
	 * @param partida
	 *            the new partida
	 */
	public void setPartida(Integer partida) {
		this.partida = partida;
	}

	/**
	 * Checks if is cuentas con saldo cero.
	 *
	 * @return true, if is cuentas con saldo cero
	 */
	public boolean isCuentasConSaldoCero() {
		return cuentasConSaldoCero;
	}

	/**
	 * Sets the cuentas con saldo cero.
	 *
	 * @param cuentasConSaldoCero
	 *            the new cuentas con saldo cero
	 */
	public void setCuentasConSaldoCero(boolean cuentasConSaldoCero) {
		this.cuentasConSaldoCero = cuentasConSaldoCero;
	}

	public ValidatePolicyService getValidatePolicyService() {
		return validatePolicyService;
	}

	public void setValidatePolicyService(ValidatePolicyService validatePolicyService) {
		this.validatePolicyService = validatePolicyService;
	}

	public ConctbRepository getConctbRepository() {
		return conctbRepository;
	}

	public void setConctbRepository(ConctbRepository conctbRepository) {
		this.conctbRepository = conctbRepository;
	}

}
