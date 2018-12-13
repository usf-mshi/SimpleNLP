-- Script to create metamap concepts table.

create table m_mm_concept
(
  rowid bigint default nextval('m_mm_concept_rowid_seq'::regclass) not null
    constraint m_mm_concept_pkey
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
  cui varchar(100),
  cui_text varchar(1000),
  semantic varchar(1000),
  sources varchar(1000),
  timestamp timestamp(0),
  begin_offset integer,
  end_offset integer
);

alter table m_mm_concept owner to postgres;

