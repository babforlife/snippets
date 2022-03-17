public class QueryByExample {
  
	public List<Vague> rechercher(RechercheDto recherche) throws ParseException {
		if (recherche.isNull()) {
			return repository.findAll(sortVague());
		}
		if (recherche.getIdAlerte() != null || recherche.getIdVague() != null || recherche.getTypeVague() != null) {
			return searchById(recherche);
		}
		return searchByFilterAndDate(recherche);
	}

	private List<Vague> searchByFilterAndDate(RechercheDto recherche) throws ParseException {
		Vague vague = new Vague();
		vague.setFlagFiltre(recherche.isFlagFiltre());
		vague.setFlagPatient(recherche.isFlagPatient());
		vague.setDateLancementVague(Utils.parseStringToDate(recherche.getDateLancementVague()));
		if (recherche.getDateExpFiltre() != null && recherche.getDateExpFiltre().equals("NULL")) {
			return repository.findAll(specificationAttributeNull(Example.of(vague), "dateExportFiltre"), sortVague());
		}
		vague.setDateExportFiltre(Utils.parseStringToDate(recherche.getDateExpFiltre()));
		return repository.findAll(Example.of(vague), sortVague());
	}

	private List<Vague> searchById(RechercheDto recherche) {
		Vague vague = new Vague();
		Alerte alerte = new Alerte();
		alerte.setIdAlerte(recherche.getIdAlerte());
		vague.setAlerte(alerte);
		vague.setIdVague(recherche.getIdVague());
		vague.setTypeVague(recherche.getTypeVague());
		return repository.findAll(Example.of(vague), sortVague());
	}

	private Sort sortVague() {
		List<Order> orders = new ArrayList<>();
		Order idAlerteOrder = new Order(Sort.Direction.DESC, "alerte.idAlerte");
		orders.add(idAlerteOrder);
		Order idVagueOrder = new Order(Sort.Direction.DESC, "idVague");
		orders.add(idVagueOrder);
		return Sort.by(orders);
	}

	// https://stackoverflow.com/questions/47805050/how-to-configure-query-by-example-to-include-null-values
	public Specification<Vague> specificationAttributeNull(Example<Vague> example, String attributeName) {
		return (root, query, builder) -> {
			final List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.isNull(root.get(attributeName)));
			predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, builder, example));
			return builder.and(predicates.toArray(new Predicate[0]));
		};

	}
}
