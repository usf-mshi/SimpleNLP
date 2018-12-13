-- Script to create ctakes symptom mention table.

create table m_ct_symptom_concept
(
  rowid bigint default nextval('m_ct_symptom_concept_rowid_seq'::regclass) not null
    constraint m_ct_symptom_concept_pkey
      primary key,
  hadm_id integer
    constraint fk_hadm_id
      references admissions (hadm_id),
  subject_id integer
    constraint fk_subject_id
      references patients (subject_id),
  dis_summary_id integer
    constraint fk_summary_id
      references m_goldstandard,
  covered_text text,
  cui varchar(50),
  cui_text varchar(1000),
  code varchar(50),
  coding_scheme varchar(50),
  tui varchar(50),
  timestamp timestamp(0),
  begin_offset integer,
  end_offset integer
);

alter table m_ct_symptom_concept owner to postgres;

