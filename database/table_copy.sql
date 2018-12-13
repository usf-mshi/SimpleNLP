-- This script can be used to copy the tables

create table m_ct_disease_concept_1
(
  like m_ct_disease_concept including all
);

create table m_ct_lab_concept_1
(
  like m_ct_lab_concept including all
);

create table m_ct_medication_concept_1
(
  like m_ct_medication_concept including all
);

create table m_ct_procedure_concept_1
(
  like m_ct_procedure_concept including all
);

create table m_ct_symptom_concept_1
(
  like m_ct_symptom_concept including all
);

create table m_mm_concept_1
(
  like m_mm_concept including all
);