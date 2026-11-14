const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validSeedPayload(uid) {
  return {
    userId: uid,
    seedValue: "-4829104851293",
    title: "جبال الزمرد والقرية المعلقة",
    edition: "java",
    minecraftVersion: "1.21.4",
    spawnBiome: "قمم متجمدة",
    spawnCoordinates: "X: 120, Y: 132, Z: -40",
    summary: "عالم غني بالموارد مع كهف عميق أسفل نقطة البداية.",
    userWishPrompt: "رسبون في جبل مع موارد كثيرة",
    highlights: ["وفرة الألماس", "قرية جبلية", "كهف أميثيست"],
    isFavorite: true,
    createdAt: new Date(),
    updatedAt: new Date(),
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write savedSeeds", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("savedSeeds").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("savedSeeds")
      .doc("seed_1")
      .set(validSeedPayload(ALICE_UID))
  );
});

test("Authenticated owner can create, list, update, and delete own savedSeed", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("savedSeeds")
    .doc("seed_1");

  await assertSucceeds(docRef.set(validSeedPayload(ALICE_UID)));
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("savedSeeds").get()
  );
  await assertSucceeds(
    docRef.update({ isFavorite: false, updatedAt: new Date() })
  );
  await assertSucceeds(docRef.delete());
});

test("Cross-user access is denied", async () => {
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("savedSeeds")
      .doc("bob_seed")
      .set(validSeedPayload(BOB_UID));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("savedSeeds")
      .doc("bob_seed")
      .get()
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("savedSeeds").get()
  );
  await assertFails(
    aliceDb
      .collection("users")
      .doc(BOB_UID)
      .collection("savedSeeds")
      .doc("bob_seed")
      .delete()
  );
});

test("Shadow fields, invalid edition, and immutable field tampering are rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("savedSeeds")
    .doc("seed_bad");

  // Shadow ghost field
  await assertFails(
    docRef.set({ ...validSeedPayload(ALICE_UID), isAdmin: true })
  );

  // Invalid edition
  await assertFails(
    docRef.set({ ...validSeedPayload(ALICE_UID), edition: "console" })
  );

  // Valid create, then immutable seedValue tampering on update
  await assertSucceeds(docRef.set(validSeedPayload(ALICE_UID)));
  await assertFails(
    docRef.update({ seedValue: "999999", updatedAt: new Date() })
  );
});
