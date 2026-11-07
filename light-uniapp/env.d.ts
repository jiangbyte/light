/// <reference types="vite/client" />
/// <reference types="@dcloudio/types" />

interface ImportMetaEnv {
  readonly VITE_API_BASE: string
  readonly UNI_PLATFORM: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
