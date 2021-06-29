alter table items_table
add column times_unlinked smallint default 0;

CREATE OR REPLACE FUNCTION increment_times_unlinked()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.times_unlinked = OLD.times_unlinked + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER items_times_unlinked
    BEFORE UPDATE ON items_table
    FOR EACH ROW
EXECUTE PROCEDURE increment_times_unlinked();
