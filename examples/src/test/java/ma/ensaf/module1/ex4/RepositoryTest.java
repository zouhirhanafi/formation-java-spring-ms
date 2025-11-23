package ma.ensaf.module1.ex4;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class RepositoryTest {

    static Repository<Student> studentRepository;

    @BeforeAll
    static void setupAll() {
        // Exécuté une seule fois avant tous les tests de cette classe
        // Cas d'utilisation : Initialiser une connexion DB, démarrer un serveur,
        // charger des données de référence coûteuses à créer
        System.out.println("Setup before all tests");
    }

    @BeforeEach
    void setup() {
        // Exécuté avant chaque test
        // Cas d'utilisation : Créer des objets de test frais, réinitialiser l'état,
        // préparer des données de test spécifiques
        System.out.println("Setup before each test");
        studentRepository = new Repository<>();
        Student s = new Student("s1", 20);
        studentRepository.save(s);
        studentRepository.save(Student.builder().name("s2").age(21).build());
    }

    @Test
    void testDeleteMustReturnTrue() {
        boolean result = studentRepository.delete(1L);
        assertThat(result).isTrue();
    }

    @Test
    void testSave() {
        // Arrange (Préparer) : Initialiser les données et dépendances
        Student s3 = Student.builder().name("s3").build();

        // Act (Agir) : Exécuter la méthode à tester
        s3 = studentRepository.save(s3);

        // Assert (Vérifier) : Vérifier que le résultat est correct
        assertThat(s3).isNotNull();
//        assertThat(s3.getId()).isNotNull();
        assertThat(s3.getId()).isEqualTo(3L);
        assertThat(studentRepository.count()).isEqualTo(3);
    }

    @Test
    void testDeleteMustReturnFalse() {
        boolean result = studentRepository.delete(10L);
        assertThat(result).isFalse();
    }

    @Test
    void testSaveArgNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> studentRepository.save(null))
                .withMessage("The entity must not be null")
        ;
    }

    @Test
    @DisplayName("Test avec un nom personnalisé")
    void testWithCustomName() {
        assertThat("Hello").isNotEmpty();
    }

    @Disabled("Test temporairement désactivé")
    @Test
    void testDisabled() {
        // Ce test ne sera pas exécuté
    }

    @AfterEach
    void tearDown() {
        // Exécuté après chaque test
        // Cas d'utilisation : Nettoyer les ressources, supprimer les fichiers temporaires,
        // réinitialiser les mocks, fermer les connexions
        System.out.println("Cleanup after each test");
    }

    @AfterAll
    static void tearDownAll() {
        // Exécuté une seule fois après tous les tests de cette classe
        // Cas d'utilisation : Fermer la connexion DB, arrêter le serveur,
        // libérer les ressources partagées
        System.out.println("Cleanup after all tests");
    }

}