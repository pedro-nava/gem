package com.gem.sistema.business.service.catalogos;

import java.util.List;

import com.gem.sistema.business.domain.Conctb;
import com.gem.sistema.business.domain.Eaid;

/**
 * @author mateo
 *
 */
public interface EaidService {
	/**
	 * @param eaid
	 * @return
	 */
	boolean save(Eaid eaid);

	/**
	 * @param eaid
	 * @return
	 */
	List<Eaid> delete(Eaid eaid);

	/**
	 * @param eaid
	 * @return
	 */
	List<Eaid> update(Eaid eaid);

	/**
	 * @param trimestre
	 * @param idSector
	 * @return
	 */
	List<Eaid> findByTrimestre(Integer trimestre, Integer idSector);

	/**
	 * @param trimestre
	 * @param condicion
	 * @return
	 */
	Eaid findByTrimestreAndCondition(Integer trimestre, String condicion, Integer idSector);

	/**
	 * @param idSector
	 * @return
	 */
	List<Eaid> findByIdSector(Integer idSector);

	/**
	 * @param idSector
	 * @param trimestre
	 * @param consecutivo
	 * @return
	 */
	Eaid findByIdSectorAndTrimestreAndCoonsecutivo(Integer idSector, Integer trimestre, Integer consecutivo);

	/**
	 * @param idSector
	 * @return
	 */
	Conctb getAnioContable(Integer idSector, long idRef);

}
