package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MemberTest {

    @Test
    public void testMemberCreationForRegularCustomer() {
        Member member = new Member("Alice", false);
        assertThat(member.name()).isEqualTo("Alice");
        assertThat(member.isMember()).isFalse();
    }

    @Test
    public void testMemberCreationForMemberCustomer() {
        Member member = new Member("Bob", true);
        assertThat(member.name()).isEqualTo("Bob");
        assertThat(member.isMember()).isTrue();
    }

}
