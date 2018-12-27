package com.gem.sistema.business.service.catalogos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gem.sistema.business.bs.Pm3711BS;
import com.gem.sistema.business.dto.Pm3711DTO;

@Service(value = "pm3711Service")
public class Pm3711ServiceImpl implements Pm3711Service {

	@Autowired
	private Pm3711BS pm3711BS;

	@Override
	public List<Pm3711DTO> listAll() {
		return pm3711BS.findAll();
	}

	@Override
	public List<Pm3711DTO> deletePm3711(Integer idRow) {

		this.pm3711BS.deletePm3711(idRow);
		return this.pm3711BS.findAll();
	}

	public Pm3711BS getPm3711BS() {
		return pm3711BS;
	}

	public void setPm3711BS(Pm3711BS pm3711bs) {
		pm3711BS = pm3711bs;
	}

}
