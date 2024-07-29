import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      backgroundImage: {
        "gradient-radial": "radial-gradient(var(--tw-gradient-stops))",
        "gradient-conic":
          "conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))",
      },
      fontSize: {
        xxs: ['9px', { lineHeight: '14px' }]
      },
    },
    screens: { // 아래의 값들은 min-width이다.
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      SD: '640px', // 480
      HD: '1366px', // 768
      FHD: '1920px', // 1080
      QHD: '2560px', // 1440
      UHD: '3840px', // 2160
    }
  },
  daisyui: {
    themes: ["light"],
  },
  plugins: [require('daisyui'),],
};
export default config;
